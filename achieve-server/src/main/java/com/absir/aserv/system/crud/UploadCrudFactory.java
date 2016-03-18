/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-9 下午3:36:15
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.*;
import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.JUpload;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.value.IUploadProcessor;
import com.absir.aserv.system.crud.value.IUploadRule;
import com.absir.aserv.system.crud.value.UploadRule;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.aserv.system.service.utils.SecurityServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
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
import com.absir.servlet.InDispathFilter;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Base
@Bean
public class UploadCrudFactory implements ICrudFactory, ICrudProcessorInput<FileItem> {

    public static final UploadCrudFactory ME = BeanFactoryUtils.get(UploadCrudFactory.class);

    public static final String RECORD = "UPLOAD@";

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    protected static final Logger LOGGER = LoggerFactory.getLogger(UploadCrudFactory.class);

    private static String uploadUrl;

    private static String uploadPath;

    @Value(value = "upload.passTime")
    private static long uploadPassTime = 3600000;

    @Value("upload.image.extension")
    private String imageExtension = "gif|jpg|jpeg|png|bmp";

    @Value("upload.manager.dir")
    private String managerDir = "";

    @Orders
    @Inject(type = InjectType.Selectable)
    private IUploadProcessor[] uploadProcessors;

    public static String getUploadUrl() {
        return uploadUrl;
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public static long getUploadPassTime() {
        return uploadPassTime;
    }

    public static FileItem getUploadFile(InputRequest input, String name) {
        List<FileItem> fileItems = input.parseParameterMap().get(name);
        return fileItems == null || fileItems.isEmpty() ? null : fileItems.get(0);
    }

    public static void verifyMultipartFile(String field, FileItem file, Object[] parameters, PropertyErrors errors) {
        String extension = HelperFileName.getExtension(file.getName()).toLowerCase();
        if (KernelString.isEmpty(extension)) {
            errors.rejectValue(field, "error file type", null);
            return;
        }

        if (parameters.length > 0) {
            Object uploadVerify = parameters[0];
            if (!(uploadVerify instanceof MultipartUploader)) {
                synchronized (parameters) {
                    if (!(parameters[0] instanceof MultipartUploader)) {
                        parameters[0] = uploadVerify = new MultipartUploader(parameters);
                    }
                }
            }

            ((MultipartUploader) uploadVerify).verify(extension, field, file, errors);

        } else {
            if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
                errors.rejectValue(field, "error file type", null);
                return;
            }

            if (Pag.CONFIGURE.getUploadSize() < file.getSize()) {
                errors.rejectValue(field, "max file size", null);
                return;
            }
        }
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public String getManagerDir() {
        return managerDir;
    }

    @Started
    protected void setUploadUrl(@Value(value = "resource.upload.url", defaultValue = "@") String
                                        uploadUrl, @Value(value = "resource.upload.path", defaultValue = "@") String uploadPath) {
        if (KernelString.isEmpty(uploadPath)) {
            return;
        }

        if (KernelString.isEmpty(uploadUrl) || uploadUrl.equals("@")) {
            uploadUrl = MenuContextUtils.getSiteRoute() + "upload/";

        } else {
            if (uploadUrl.indexOf(':') <= 0) {
                uploadUrl = HelperFileName.concat(MenuContextUtils.getSiteRoute(), uploadUrl);
            }
        }

        if (KernelString.isEmpty(uploadPath) || uploadPath.equals("@")) {
            uploadPath = InDispathFilter.getContextResourcePath() + "upload/";

        } else {
            uploadPath = HelperFileName.concat(InDispathFilter.getContextResourcePath(), uploadPath);
        }

        UploadCrudFactory.uploadUrl = uploadUrl;
        UploadCrudFactory.uploadPath = uploadPath;
    }


    public String randUploadFile(int hashCode) {
        Date date = new Date();
        //todo 随机命名需要Sequence
        return DATE_FORMAT.format(date) + '/' + HelperRandom.randSecondId(date.getTime(), 16, hashCode);
    }

    public void upload(String uploadFile, InputStream inputStream) throws IOException {
        HelperFile.write(new File(uploadPath + uploadFile), inputStream);
    }

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
        if (KernelString.isEmpty(extension)) {
            throw new ServerException(ServerStatus.ON_DENIED, "extension");
        }

        extension = extension.toLowerCase();
        if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
            throw new ServerException(ServerStatus.ON_DENIED, "extension");
        }

        long fileSize = inputStream.available();
        if (Pag.CONFIGURE.getUploadSize() < fileSize) {
            throw new ServerException(ServerStatus.ON_DENIED, "size");
        }

        JUpload upload = new JUpload();
        upload.setFileType(extension);
        upload.setFileSize(fileSize);
        inputStream = uploadProcessor(extension, upload, inputStream);
        String uploadFile = randUploadFile(inputStream.hashCode()) + '.' + extension;
        upload(uploadFile, inputStream);
        long contextTime = ContextUtils.getContextTime();
        upload.setCreateTime(contextTime);
        upload.setPassTime(contextTime + uploadPassTime);
        if (user != null) {
            upload.setUserId(user.getUserId());
        }

        upload.setFilename(uploadFile);
        CrudServiceUtils.merge("JUpload", null, upload, true, user, null);
        return uploadFile;
    }

    /**
     * 上传文件处理(加水印,压缩,加密等)
     *
     * @param extension
     * @param upload
     * @param inputStream
     * @return
     */
    protected InputStream uploadProcessor(String extension, JUpload upload, InputStream inputStream) {
        if (uploadProcessors != null) {
            for (IUploadProcessor uploadProcessor : uploadProcessors) {
                inputStream = uploadProcessor.process(extension, upload, inputStream);
                if (upload.getFileType() != null) {
                    break;
                }
            }

            if (inputStream == null) {
                throw new ServerException(ServerStatus.ON_DENIED, "process");
            }
        }

        return inputStream;
    }

    /**
     * 创建数据库目录
     *
     * @param dirPath
     * @param userId
     * @param createTime
     */
    protected void createDirpath(String dirPath, long userId, long createTime) {
        dirPath = HelperFileName.getFullPathNoEndSeparator(dirPath);
        String dir = HelperFileName.getPath(dirPath);
        if (KernelString.isEmpty(dir)) {
            dir = "";

        } else {
            dirPath = dirPath.substring(dir.length());
        }

        if (BeanService.ME.selectQuerySingle("SELECT o.id FROM JUpload o WHERE o.dirPath = ? AND o.filename = ?", dir, dirPath) == null) {
            JUpload upload = new JUpload();
            upload.setDirPath(dir);
            upload.setFilename(dirPath);
            upload.setUserId(userId);
            upload.setCreateTime(createTime);
            BeanService.ME.persist(upload);
            if (dir != "") {
                createDirpath(dir, userId, createTime);
            }
        }
    }

    /**
     * JUpload实体处理
     *
     * @param upload
     * @param crud
     * @param handler
     */
    public void crud(JUpload upload, Crud crud, CrudHandler handler) {
        if (KernelString.isEmpty(upload.getFileType())) {
            return;
        }

        if (crud == Crud.CREATE) {
            upload.setImaged(imageExtension.contains(upload.getFileType()));
            String filename = upload.getFilename();
            String dirPath = HelperFileName.getPath(filename);
            if (KernelString.isEmpty(dirPath)) {
                upload.setDirPath("");

            } else {
                createDirpath(dirPath, SecurityServiceUtils.getUserId(), System.currentTimeMillis());
                upload.setDirPath(dirPath);
                upload.setFilename(filename.substring(dirPath.length()));
            }

        } else if (crud == Crud.DELETE) {
            Session session = BeanDao.getSession();
            Iterator iterate = QueryDaoUtils.createQueryArray(session, "SELECT o.id FROM JUploadCite o WHERE o.upload.id = ?", upload.getId()).iterate();
            if (iterate.hasNext()) {
                session.cancelQuery();
                upload.setPassTime(0);
                session.merge(upload);
            }
        }
    }

    /**
     * 上传文件
     *
     * @param user
     * @param request
     * @return
     */
    public List<String> uploads(JiUserBase user, HttpServletRequest request) throws IOException, FileUploadException {
        List<String> paths = new ArrayList<String>();
        FileItemIterator iterator = InputRequest.SERVLET_FILE_UPLOAD_DEFAULT.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream fileItem = iterator.next();
            if (!KernelString.isEmpty(fileItem.getName())) {
                paths.add(UploadCrudFactory.ME.uploadExtension(HelperFileName.getExtension(fileItem.getName()), fileItem.openStream(), user));
            }
        }

        return paths;
    }

    /**
     * 获取管理文件路径
     *
     * @param path
     * @return
     */
    public String getDirPath(String path) {
        if (KernelString.isEmpty(path) || path.equals("/")) {
            path = "";

        } else {
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }

            if (path.charAt(path.length() - 1) != '/') {
                path = path + '/';
            }
        }

        if (!KernelString.isEmpty(managerDir)) {
            path = managerDir + path;
        }

        return path;
    }

    /**
     * 文件管理
     *
     * @param path
     * @param order
     * @return
     */
    public List<JUpload> list(String path, String order) {
        path = getDirPath(path);
        String listHql = null;
        if (order != null) {
            order = order.toLowerCase();
            if (order.equals("name")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.filename";

            } else if (order.equals("type")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.fileType";

            } else if (order.equals("size")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.fileSize";
            }
        }

        if (listHql == null) {
            listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ?";
        }

        return (List<JUpload>) BeanService.ME.selectQuery(listHql, path);
    }

    /**
     * 检测文件夹是否为空
     *
     * @param path
     * @return
     */
    public boolean isEmpty(String path) {
        path = getDirPath(path);
        return BeanService.ME.selectQuerySingle("SELECT o.id FROM JUpload o WHERE o.dirPath = ?", path) == null;
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

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
                                    ruleName = "entity/" + ruleName;
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

                        uploadFile = HelperString.replaceEach(multipartUploader.ruleName, new String[]{":name", ":id", ":ext"}, new String[]{crudProperty.getName(), identity, extensionName});
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
                    uploadFile = randUploadFile(requestBody.hashCode()) + '.' + extensionName;
                }

                if (uploadStream == null) {
                    uploadStream = requestBody.getInputStream();
                }

                uploadStream = uploadProcessor(extensionName, null, uploadStream);
                upload(uploadFile, uploadStream);

            } catch (IOException e) {
                LOGGER.error("upload error", e);
            }

            crudProperty.set(entity, uploadFile);
        }
    }

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
        if (crudHandler.getCrud() == Crud.DELETE) {
            String uploadFile = (String) crudProperty.get(entity);
            if (!KernelString.isEmpty(uploadFile)) {
                delete(uploadFile);
            }
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return ME;
    }

    public static class MultipartUploader {

        private long minSize;

        private long maxSize;

        private String[] extensions;

        private String ruleName;

        private boolean ided;

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

        public void verify(String extension, String field, FileItem file, PropertyErrors errors) {
            if (extensions != null) {
                if (!KernelArray.contain(extensions, extension)) {
                    errors.rejectValue(field, "error file type", null);
                    return;
                }

            } else {
                if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
                    errors.rejectValue(field, "error file type", null);
                    return;
                }
            }

            if (maxSize > 0) {
                if (file.getSize() > maxSize) {
                    errors.rejectValue(field, "max file size", null);
                    return;
                }

            } else {
                if (Pag.CONFIGURE.getUploadSize() < file.getSize()) {
                    errors.rejectValue(field, "max file size", null);
                    return;
                }
            }

            if (minSize > 0 && file.getSize() < minSize) {
                errors.rejectValue(field, "min file size", null);
                return;
            }
        }
    }
}
