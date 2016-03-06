/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-9 下午3:36:15
 */
package com.absir.aserv.system.crud;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.crud.ICrudProcessorInput;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.JEmbedSL;
import com.absir.aserv.system.bean.JUpload;
import com.absir.aserv.system.bean.JUploadCite;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.AuthService;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
@Base
@Bean
public class RichCrudFactory implements ICrudFactory, ICrudProcessorInput<Object> {

	/** ME */
	public static final RichCrudFactory ME = BeanFactoryUtils.get(RichCrudFactory.class);

	/** UPLOAD */
	public static final String UPLOAD = "upload";

	/** RECORD */
	public static final String RECORD = "RICH@";

	/** REMOTE_RICH_NAME */
	private static final String REMOTE_RICH_NAME = "REMOTE_RICH@";

	/**
	 * 获取关联ID
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	public static String getAssocId(String entityName, Object id) {
		if (id != null) {
			return entityName + "@" + DynaBinderUtils.getParamFromValue(id);
		}

		return null;
	}

	/**
	 * 获取关联ID
	 * 
	 * @param handler
	 * @return
	 */
	public static String getAssocId(CrudHandler handler) {
		String entityName = handler.getCrudEntity().getJoEntity().getEntityName();
		if (entityName != null) {
			Object id = CrudService.ME.getCrudSupply(entityName).getIdentifier(entityName, handler.getRoot());
			return getAssocId(entityName, id);
		}

		return null;
	}

	/**
	 * @param session
	 * @param assocId
	 * @return
	 */
	public static List<JUploadCite> getUploadCites(Session session, String assocId) {
		return QueryDaoUtils.createQueryArray(session, "SELECT o FROM JUploadCite o WHERE o.id.eid = ?", assocId).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessorInput#isMultipart()
	 */
	@Override
	public boolean isMultipart() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud.
	 * CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
	@Transaction
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user) {
		if (handler.getCrud() == Crud.DELETE) {
			String assocId = getAssocId(handler);
			if (assocId != null) {
				Session session = BeanDao.getSession();
				List<JUploadCite> uploadCites = getUploadCites(session, assocId);
				if (!uploadCites.isEmpty()) {
					QueryDaoUtils.createQueryArray(session, "DELETE o FROM JUploadCite o WHERE o.id.eid = ?", assocId).executeUpdate();
					session.flush();
					long contextTime = ContextUtils.getContextTime();
					for (JUploadCite uploadCite : uploadCites) {
						JUpload upload = uploadCite.getUpload();
						upload.setPassTime(contextTime + UploadCrudFactory.getUploadPassTime());
						session.merge(upload);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudProcessorInput#crud(com.absir.aserv.crud
	 * .CrudProperty, com.absir.property.PropertyErrors,
	 * com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.server.in.Input)
	 */
	@Override
	public Object crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input) {
		if (handler.getCrud() != Crud.DELETE && AuthService.ME.menuPermission(UPLOAD, user)) {
			Object remote = input.getParamMap().get(REMOTE_RICH_NAME);
			if (remote == null) {
				remote = input.getParamMap().get(REMOTE_RICH_NAME);
			}

			if (DynaBinder.to(remote, boolean.class)) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/** IMG_SRC_PATTERN */
	public static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img[^<>]*?[\\s| ]{1}src=[\"']{1}([^\"']*)[\"']{1}", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/** SRC_EXCLUDE_CHARS */
	public static final char[] SRC_EXCLUDE_CHARS = new char[] { '\'', '"', '\r', '\n' };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudProcessorInput#crud(com.absir.aserv.crud
	 * .CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase, java.lang.Object)
	 */
	@Transaction
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, Object inputBody) {
		if (handler.getCrud() != Crud.DELETE) {
			String value = (String) crudProperty.get(entity);
			if (value != null) {
				// 远程下载图片
				if (inputBody == Boolean.TRUE) {
					int end = 0;
					StringBuilder stringBuilder = new StringBuilder();
					Matcher matcher = IMG_SRC_PATTERN.matcher(value);
					char[] chars = value.toCharArray();
					while (matcher.find()) {
						String src = matcher.group(1);
						if (!src.startsWith(UploadCrudFactory.getUploadUrl())) {
							String find = matcher.group();
							String replace = UploadCrudFactory.ME.remoteDownload(src, "jpg", user);
							if (replace == null) {
								stringBuilder.append(chars, end, matcher.end() - end);

							} else {
								replace = UploadCrudFactory.getUploadUrl() + replace;
								stringBuilder.append(chars, end, matcher.start() - end);
								stringBuilder.append(find.replace(src, replace));
							}

							end = matcher.end();
						}
					}

					if (end != 0) {
						int len = chars.length - end;
						if (len > end) {
							stringBuilder.append(chars, end, len - end);
						}

						crudProperty.set(entity, stringBuilder.toString());
					}
				}

				// 查找关联文件
				Set<String> srcs = new HashSet<String>();
				int end = 0;
				int length = value.length();
				String uploadUrl = UploadCrudFactory.getUploadUrl();
				int len = uploadUrl.length();
				while (end < length) {
					int pos = value.indexOf(uploadUrl, end);
					if (pos > 0) {
						end = pos + len;
						char chr = value.charAt(pos - 1);
						if (chr == '\'' || chr == '"') {
							pos = value.indexOf(chr, end);
							if (pos > 0) {
								int last = pos - end - 1;
								if (last > 1) {
									String src = value.substring(end, last);
									if (HelperString.indexOfAny(src, SRC_EXCLUDE_CHARS) < 0) {
										srcs.add(src);
									}
								}

								end = pos + 1;
							}
						}

					} else {
						break;
					}
				}

				String entityName = handler.getCrudEntity().getJoEntity().getEntityName();
				Object id = CrudServiceUtils.identifier(entityName, entity, handler.isCreate());
				if (id != null) {
					String assocId = getAssocId(entityName, id);
					Session session = BeanDao.getSession();
					if (handler.getCrud() == Crud.UPDATE) {
						if (handler.getCrudRecord() != null && !handler.getCrudRecord().containsKey(RECORD)) {
							handler.getCrudRecord().put(RECORD, Boolean.TRUE);
							long contextTime = ContextUtils.getContextTime();
							List<JUploadCite> uploadCites = getUploadCites(session, assocId);
							for (JUploadCite uploadCite : uploadCites) {
								JUpload upload = uploadCite.getUpload();
								if (!srcs.contains(upload.getFilePath())) {
									session.delete(uploadCite);
									upload.setPassTime(contextTime + UploadCrudFactory.getUploadPassTime());
								}
							}
						}
					}

					for (String src : srcs) {
						Iterator<JUpload> iterator = QueryDaoUtils.createQueryArray(session, "SELECT o FROM JUpload o WHERE o.filePath = ?", src).iterate();
						if (iterator.hasNext()) {
							JUpload upload = iterator.next();
							upload.setPassTime(0);
							session.merge(upload);
							JUploadCite uploadCite = new JUploadCite();
							uploadCite.setId(new JEmbedSL(assocId, upload.getId()));
							uploadCite.setUpload(upload);
							session.merge(uploadCite);
						}
					}
				}
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
		return CharSequence.class.isAssignableFrom(crudField.getType()) ? ME : null;
	}
}
