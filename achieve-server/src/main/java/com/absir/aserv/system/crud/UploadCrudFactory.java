/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-9 下午3:36:15
 */
package com.absir.aserv.system.crud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.crud.ICrudProcessorInput;
import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.JUpload;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.value.IUploadProcessor;
import com.absir.aserv.system.crud.value.IUploadRule;
import com.absir.aserv.system.crud.value.UploadRule;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperClient;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
@Base
@Bean
public class UploadCrudFactory implements ICrudFactory, ICrudProcessorInput<FileItem> {

	/** ME */
	public static final UploadCrudFactory ME = BeanFactoryUtils.get(UploadCrudFactory.class);

	/** RECORD */
	public static final String RECORD = "UPLOAD@";

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(UploadCrudFactory.class);

	/** uploadUrl */
	private static String uploadUrl;

	/** uploadPath */
	private static String uploadPath;

	/** uploadPassTime */
	@Value(value = "upload.passTime")
	private static long uploadPassTime = 3600000;

	/**
	 * @return
	 */
	public static String getUploadUrl() {
		return uploadUrl;
	}

	/**
	 * @return
	 */
	public static String getUploadPath() {
		return uploadPath;
	}

	/**
	 * @return the uploadPassTime
	 */
	public static long getUploadPassTime() {
		return uploadPassTime;
	}

	/**
	 * @param name
	 * @param fileItem
	 * @return
	 */
	public static FileItem getUploadFile(InputRequest input, String name) {
		List<FileItem> fileItems = input.parseParameterMap().get(name);
		return fileItems == null || fileItems.isEmpty() ? null : fileItems.get(0);
	}

	/**
	 * @param uploadUrl
	 * @param uploadPath
	 */
	@Started
	protected void setUploadUrl(@Value(value = "resource.upload.url", defaultValue = "upload") String uploadUrl, @Value(value = "resource.upload.path", defaultValue = "upload") String uploadPath) {
		if (KernelString.isEmpty(uploadPath)) {
			return;
		}

		UploadCrudFactory.uploadUrl = uploadUrl;
		UploadCrudFactory.uploadPath = HelperFileName.normalizeNoEndSeparator(BeanFactoryUtils.getBeanConfig().getResourcePath() + uploadPath) + "/";
	}

	/** DATE_FORMAT */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	/**
	 * @param hashCode
	 * @return
	 */
	public String randUploadFile(int hashCode) {
		Date date = new Date();
		return DATE_FORMAT.format(date) + '/' + HelperRandom.randSecondId((int) date.getTime(), 16, hashCode);
	}

	/**
	 * @param uploadFile
	 * @param inputStream
	 * @throws IOException
	 */
	public void upload(String uploadFile, InputStream inputStream) throws IOException {
		HelperFile.write(new File(uploadPath + uploadFile), inputStream);
	}

	/**
	 * @param uploadFile
	 */
	public void delete(String uploadFile) {
		HelperFile.deleteQuietly(new File(uploadPath + uploadFile));
	}

	/**
	 * 远程下载
	 * 
	 * @param url
	 * @param defaultExtension
	 * @param user
	 * @return
	 */
	public String remoteDownload(String url, String defaultExtension, JiUserBase user) {
		String extension = HelperFileName.getExtension(url);
		if (KernelString.isEmpty(extension)) {
			extension = defaultExtension;
			if (KernelString.isEmpty(extension)) {
				return null;
			}
		}

		try {
			return uploadExtension(extension, HelperClient.openConnection((HttpURLConnection) (new URL(url)).openConnection()), user);

		} catch (Throwable e) {
			return null;
		}
	}

	/** uploadProcessors */
	@Orders
	@Inject(type = InjectType.Selectable)
	private IUploadProcessor[] uploadProcessors;

	/**
	 * 上传扩展名内容
	 * 
	 * @param extension
	 * @param inputStream
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public String uploadExtension(String extension, InputStream inputStream, JiUserBase user) throws IOException {
		extension = extension.toLowerCase();
		if (Pag.CONFIGURE.getUploadSize() < inputStream.available()) {
			throw new ServerException(ServerStatus.ON_DENIED, "size");
		}

		if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
			throw new ServerException(ServerStatus.ON_DENIED, "extension");
		}

		JUpload upload = new JUpload();
		upload.setFilePath(extension);
		if (uploadProcessors != null) {
			for (IUploadProcessor uploadProcessor : uploadProcessors) {
				inputStream = uploadProcessor.process(upload, inputStream);
				if (upload.getFileType() != null) {
					break;
				}
			}

			if (inputStream == null) {
				throw new ServerException(ServerStatus.ON_DENIED, "process");
			}
		}

		extension = upload.getFilePath();
		String uploadFile = randUploadFile(inputStream.hashCode()) + '.' + extension;
		upload(uploadFile, inputStream);
		if (upload.getFileType() == null) {
			upload.setFileType(extension);
		}

		long contextTime = ContextUtils.getContextTime();
		upload.setCreateTime(contextTime);
		upload.setPassTime(contextTime + uploadPassTime);
		if (user != null) {
			upload.setUserId(user.getUserId());
		}

		return uploadFile;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class MultipartUploader {

		/** minSize */
		private long minSize;

		/** maxSize */
		private long maxSize;

		/** extensions */
		private String[] extensions;

		/** ruleName */
		private String ruleName;

		/** ided */
		private boolean ided;

		/**
		 * @param parameters
		 */
		public MultipartUploader(Object[] parameters) {
			int last = parameters.length - 1;
			if (last > 2) {
				last = 2;
			}

			for (int i = 0; last >= 0; i++, last--) {
				switch (i) {
				case 0:
					Object extension = parameters[last];
					if (extension instanceof String && !"".equals(extension)) {
						extensions = ((String) extension).toLowerCase().split(",");
					}
					break;

				case 1:
					maxSize = (long) (KernelDyna.to(parameters[last], float.class) * 1024);
					break;

				case 3:
					minSize = (long) (KernelDyna.to(parameters[last], float.class) * 1024);
					break;

				default:
					break;
				}
			}
		}

		/**
		 * @param field
		 * @param file
		 * @param errors
		 */
		public void verify(String field, FileItem file, PropertyErrors errors) {
			if (extensions != null && !KernelArray.contain(extensions, HelperFileName.getExtension(file.getName()).toLowerCase())) {
				errors.rejectValue(field, "error file type", null);
				return;
			}

			if (maxSize > 0 && file.getSize() > maxSize) {
				errors.rejectValue(field, "max file size", null);
				return;
			}

			if (minSize > 0 && file.getSize() < minSize) {
				errors.rejectValue(field, "min file size", null);
				return;
			}
		}
	}

	/**
	 * @param field
	 * @param file
	 * @param parameters
	 * @param errors
	 */
	public static void verifyMultipartFile(String field, FileItem file, Object[] parameters, PropertyErrors errors) {
		if (parameters.length > 0) {
			Object uploadVerify = parameters[0];
			if (!(uploadVerify instanceof MultipartUploader)) {
				synchronized (parameters) {
					if (!(parameters[0] instanceof MultipartUploader)) {
						parameters[0] = uploadVerify = new MultipartUploader(parameters);
					}
				}
			}

			((MultipartUploader) uploadVerify).verify(field, file, errors);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessorRequest#isMultipart()
	 */
	@Override
	public boolean isMultipart() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessorInput#crud(com.absir.aserv
	 * .crud.CrudProperty, com.absir.property.PropertyErrors,
	 * com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.server.in.Input)
	 */
	@Override
	public FileItem crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input) {
		if (handler.getCrud() != Crud.DELETE) {
			String field = handler.getFilter().getPropertyPath();
			if (input instanceof InputRequest) {
				FileItem file = getUploadFile((InputRequest) input, field + "_file");
				if (file != null && !KernelString.isEmpty(file.getName())) {
					verifyMultipartFile(field, file, crudProperty.getjCrud().getParameters(), errors);
					return file;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessorRequest#crud(com.absir.aserv
	 * .crud.CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase, java.lang.Object)
	 */
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, FileItem requestBody) {
		if (requestBody == null) {
			if (handler.getCrudRecord() != null) {
				String uploadFile = (String) crudProperty.get(entity);
				if (!KernelString.isEmpty(uploadFile)) {
					handler.getCrudRecord().put(RECORD + uploadFile, Boolean.TRUE);
				}
			}

		} else {
			Field field = crudProperty.getAccessor().getField();
			if (field != null && field.getType().isAssignableFrom(FileItem.class)) {
				crudProperty.set(entity, requestBody);
				return;
			}

			String uploadFile = (String) crudProperty.get(entity);
			if (!KernelString.isEmpty(uploadFile)) {
				if (handler.getCrudRecord() == null || !handler.getCrudRecord().containsKey(RECORD + uploadFile)) {
					delete(uploadFile);
				}
			}

			InputStream uploadStream = null;
			String extensionName = HelperFileName.getExtension(requestBody.getName());
			try {
				Object[] parameters = crudProperty.getjCrud().getParameters();
				MultipartUploader multipartUploader = parameters.length == 0 ? null : (MultipartUploader) parameters[0];
				if (multipartUploader != null) {
					if (multipartUploader.ruleName == null) {
						String ruleName = null;
						Accessor accessor = crudProperty.getAccessor();
						if (accessor != null) {
							UploadRule uploadRule = accessor.getAnnotation(UploadRule.class, false);
							if (uploadRule != null) {
								ruleName = uploadRule.value();
								multipartUploader.ided = ruleName.contains(":id");
								if (KernelString.isEmpty(HelperFileName.getPath(ruleName))) {
									ruleName = "data/" + ruleName;
								}
							}
						}

						multipartUploader.ruleName = ruleName == null ? "" : ruleName;
					}

					if ("".equals(multipartUploader.ruleName)) {
						multipartUploader = null;

					} else {
						String identity = "";
						if (multipartUploader.ided) {
							Object id = CrudServiceUtils.identifier(handler.getCrudEntity().getJoEntity().getEntityName(), entity, handler.isCreate());
							if (id != null) {
								identity = DynaBinderUtils.getParamFromValue(id);
							}
						}

						uploadFile = HelperString.replaceEach(multipartUploader.ruleName, new String[] { ":name", ":id", ":ext" }, new String[] { crudProperty.getName(), identity, extensionName });
					}
				}

				if (multipartUploader == null && entity instanceof IUploadRule) {
					IUploadRule uploadRule = (IUploadRule) entity;
					uploadFile = uploadRule.getUploadRuleName(crudProperty.getName(), extensionName);
					if (uploadFile != null) {
						uploadStream = uploadRule.proccessInputStream(crudProperty.getName(), requestBody.getInputStream(), extensionName);
					}
				}

				if (KernelString.isEmpty(uploadFile)) {
					uploadFile = randUploadFile(requestBody.hashCode()) + "." + HelperFileName.getExtension(requestBody.getName());
				}

				if (uploadStream == null) {
					uploadStream = requestBody.getInputStream();
				}

				upload(uploadFile, uploadStream);

			} catch (IOException e) {
				LOGGER.error("upload error", e);
			}

			crudProperty.set(entity, uploadFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud
	 * .CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
		if (crudHandler.getCrud() == Crud.DELETE) {
			String uploadFile = (String) crudProperty.get(entity);
			if (!KernelString.isEmpty(uploadFile)) {
				delete(uploadFile);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudFactory#getProcessor(com.absir.aserv.support
	 * .entity.value.JoEntity, com.absir.aserv.support.developer.JCrudField)
	 */
	@Override
	public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
		return ME;
	}
}
