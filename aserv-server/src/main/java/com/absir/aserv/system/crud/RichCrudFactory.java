/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-9 下午3:36:15
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.*;
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
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
@Base
@Bean
public class RichCrudFactory implements ICrudFactory, ICrudProcessorInput<Object> {

    public static final RichCrudFactory ME = BeanFactoryUtils.get(RichCrudFactory.class);

    public static final String UPLOAD = "upload";

    public static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img[^<>]*?[\\s| ]{1}src=[\"']{1}([^\"']*)[\"']{1}", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static final char[] SRC_EXCLUDE_CHARS = new char[]{'\'', '"', '\r', '\n'};

    private static final String REMOTE_RICH_NAME = "@REMOTE_RICH";

    /**
     * 获取关联ID
     *
     * @param entityName
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

    public static List<JUploadCite> getUploadCites(Session session, String assocId) {
        return QueryDaoUtils.createQueryArray(session, "SELECT o FROM JUploadCite o WHERE o.id.eid = ?", assocId).list();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Transaction
    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, Input input) {
        if (handler.getCrud() == Crud.DELETE) {
            String assocId = getAssocId(handler);
            if (assocId != null) {
                Session session = BeanDao.getSession();
                List<JUploadCite> uploadCites = getUploadCites(session, assocId);
                if (!uploadCites.isEmpty()) {
                    QueryDaoUtils.createQueryArray(session, "DELETE FROM JUploadCite o WHERE o.id.eid = ?", assocId).executeUpdate();
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

    @Override
    public Object crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input) {
        return input;
    }

    @Transaction
    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, Object inputBody) {
        Session session = BeanDao.getSession();
        Object id = CrudUtils.identifier(handler, entity);
        String entityName = handler.getCrudEntity().getJoEntity().getEntityName();
        String assocId = getAssocId(entityName, id);
        if (handler.getCrud() == Crud.DELETE) {
            // 解除关联关系
            QueryDaoUtils.createQueryArray(session, "DELETE FROM JUploadCite o WHERE o.id.eid = ?", assocId).executeUpdate();

        } else {
            boolean downloadPermission = AuthService.ME.menuPermission(UPLOAD, user);
            Input input = (Input) inputBody;
            Set<String> srcs = new HashSet<String>();
            // 处理所有RichHtml字段
            if (crudProperty.getjCrud().getParameters().length == 0) {
                richHtml(handler.getFilter().getPropertyPath(), crudProperty.getName(), entity, srcs, downloadPermission, user, input);

            } else {
                for (Object field : crudProperty.getjCrud().getParameters()) {
                    richHtml(handler.getFilter().getPropertyPath(), (String) field, entity, srcs, downloadPermission, user, input);
                }
            }

            // 增加关联和设置关联为不过期
            long updateTime = System.currentTimeMillis();
            for (String src : srcs) {
                Iterator<JUpload> iterator = QueryDaoUtils.createQueryArray(session, "SELECT o FROM JUpload o WHERE o.filePath = ?", src).iterate();
                if (iterator.hasNext()) {
                    JUpload upload = iterator.next();
                    upload.setPassTime(0);
                    session.merge(upload);
                    JUploadCite uploadCite = new JUploadCite();
                    uploadCite.setId(new JEmbedSL(assocId, upload.getId()));
                    uploadCite.setUpload(upload);
                    uploadCite.setUpdateTime(updateTime);
                    session.merge(uploadCite);
                }
            }

            QueryDaoUtils.createQueryArray(session, "DELETE FROM JUploadCite o WHERE o.id.eid = ? AND o.updateTime != ?", assocId, updateTime).executeUpdate();
        }

        if (handler.getCrud() != Crud.CREATE) {
            // 添加关联文件过期
            long passTime = ContextUtils.getContextTime() + UploadCrudFactory.getUploadPassTime();
            QueryDaoUtils.createQueryArray(session, "UPDATE JUpload o SET o.passTime = ? WHERE o.id IN (SELECT o.id.mid FROM JUploadCite o WHERE o.id.eid = ?)", passTime, assocId).executeUpdate();
        }
    }

    /**
     * 富文本自动下载和获取关联链接
     */
    protected void richHtml(String propertyPath, String field, Object entity, Set<String> srcs, boolean downloadPermission, JiUserBase user, Input input) {
        UtilAccessor.Accessor accessor = UtilAccessor.getAccessor(entity.getClass(), field, null);
        if (accessor == null) {
            return;
        }

        String html = (String) accessor.get(entity);
        if (KernelString.isEmpty(html)) {
            return;
        }

        // 远程下载图片
        if (downloadPermission && input != null && KernelDyna.toBoolean(input.getParam(propertyPath + '.' + field + REMOTE_RICH_NAME))) {
            int end = 0;
            StringBuilder stringBuilder = new StringBuilder();
            Matcher matcher = IMG_SRC_PATTERN.matcher(html);
            char[] chars = html.toCharArray();
            while (matcher.find()) {
                String src = matcher.group(1);
                if (!UploadCrudFactory.ME.isUploadUrl(src)) {
                    String find = matcher.group();
                    String replace = UploadCrudFactory.ME.remoteDownload(src, "jpg", user);
                    if (replace == null) {
                        stringBuilder.append(chars, end, matcher.end() - end);

                    } else {
                        replace = UploadCrudFactory.ME.getUploadUrl(replace);
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

                accessor.set(entity, stringBuilder.toString());
            }
        }

        // 查找关联文件
        int end = 0;
        int length = html.length();
        String uploadUrl = UploadCrudFactory.ME.forUploadUrl();
        int len = uploadUrl.length();
        while (end < length) {
            int pos = html.indexOf(uploadUrl, end);
            if (pos > 0) {
                end = pos + len;
                char chr = html.charAt(pos - 1);
                if (chr == '\'' || chr == '"') {
                    pos = html.indexOf(chr, end);
                    if (pos > 0) {
                        int last = pos - end - 1;
                        if (last > 1) {
                            String src = html.substring(end, last);
                            if (HelperString.indexOfAny(src, SRC_EXCLUDE_CHARS) < 0) {
                                if (src.startsWith(uploadUrl)) {
                                    srcs.add(src.substring(uploadUrl.length()));
                                }
                            }
                        }

                        end = pos + 1;
                    }
                }

            } else {
                break;
            }
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return CharSequence.class.isAssignableFrom(crudField.getType()) ? ME : null;
    }
}
