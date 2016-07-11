/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.admin;

import com.absir.aserv.configure.xls.XlsAccessorUtils;
import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.aserv.crud.*;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.menu.value.MaPermission;
import com.absir.aserv.system.bean.JLog;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.RichCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.EntityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.aserv.system.service.utils.InputServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.binder.BinderUtils;
import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.parameter.ParameterResolverBinder;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Base
@Server
public class Admin_entity extends AdminServer {

    /**
     * CRUDSupply统一入口
     */
    protected ICrudSupply getCrudSupply(String entityName, Input input) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        if (crudSupply == null) {
            throw new ServerException(ServerStatus.IN_404);
        }

        if (input != null) {
            JoEntity joEntity = new JoEntity(entityName, crudSupply.getEntityClass(entityName));
            input.setAttribute("joEntity", joEntity);
        }

        return crudSupply;
    }

    /**
     * 列表页面
     */
    public void list(String entityName, @Binder JdbcPage jdbcPage, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (!crudSupply.support(Crud.COMPLETE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
        InModel model = input.getModel();
        try {
            model.put("filter", AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user));
            model.put("update", true);

        } catch (ServerException e) {
            model.put("update", false);
        }

        model.put("insert", AuthServiceUtils.insertPermission(crudSupply, entityName, user));
        model.put("delete", AuthServiceUtils.deletePermission(crudSupply, entityName, user));
        jdbcPage = InputServiceUtils.getJdbcPage(entityName, jdbcPage, input);
        model.put("page", jdbcPage);
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        String queue = InputServiceUtils.getOrderQueue(entityName, input);
        model.put("entities", EntityService.ME.list(entityName, crudSupply, user, null,
                InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), filter, null, input),
                queue, jdbcPage));
    }

    public void edit(String entityName, @Nullable @Param FileItem xls, Input input) {
        edit(entityName, null, xls, input);
    }

    /**
     * 编辑页面
     */
    public void edit(String entityName, Object id, @Nullable @Param FileItem xls, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (id == null && !crudSupply.support(Crud.CREATE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = null;
        InModel model = input.getModel();
        try {
            if (id == null) {
                filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);

            } else {
                filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
            }

            model.put("update", true);

        } catch (ServerException e) {
            if (!AuthServiceUtils.selectPermission(entityName, user)) {
                throw new ServerException(ServerStatus.ON_DENIED);
            }

            filter = new PropertyFilter();
            filter.exlcude("*");
            model.put("update", false);
        }

        model.put("insert", AuthServiceUtils.insertPermission(crudSupply, entityName, user));
        model.put("delete", AuthServiceUtils.deletePermission(crudSupply, entityName, user));
        model.put("create", id == null && crudSupply.support(Crud.CREATE));
        JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
        model.put("multipart", CrudContextUtils.isMultipart(joEntity));
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        Object entity = edit(entityName, id, crudSupply, user);
        if (xls != null) {
            if (!xls.getName().toLowerCase().endsWith(".xls")) {
                model.put("xls", 1);

            } else {
                try {
                    HSSFWorkbook workbook = new HSSFWorkbook(xls.getInputStream());
                    if (!XlsAccessorUtils.isHead(workbook, crudSupply.getEntityClass(entityName))) {
                        model.put("xls", 2);

                    } else {
                        List<?> entities = XlsUtils.getXlsList(workbook, null, crudSupply.getEntityClass(entityName),
                                XlsUtils.XLS_BASE, false);
                        if (entities.size() > 0) {
                            KernelObject.clone(entities.get(0), entity);
                        }
                    }

                } catch (Exception e) {
                    model.put("xls", 3);
                }
            }
        }

        BinderData binderData = input.getBinderData();
        BinderResult binderResult = binderData.getBinderResult();
        binderResult.setValidation(true);
        binderResult.setPropertyFilter(filter);
        binderData.mapBind(BinderUtils.getDataMap(input.getParamMap()), entity);
        CrudContextUtils.crud(Crud.CREATE, false, null, joEntity, entity, user, filter);
        model.put("entity", entity);
    }

    /**
     * 获取编辑实体
     */
    private Object edit(String entityName, Object id, ICrudSupply crudSupply, JiUserBase user) {
        Object entity = null;
        if (id == null) {
            return entity = crudSupply.create(entityName);

        } else {
            Serializable identifier = DynaBinderUtils.getParamId(id, crudSupply.getIdentifierType(entityName));
            if (identifier == null) {
                throw new ServerException(ServerStatus.IN_404);
            }

            entity = crudSupply.get(entityName, identifier, AccessServiceUtils.updateCondition(entityName, user, null));
            if (entity == null) {
                if (crudSupply.get(entityName, identifier, null) == null) {
                    throw new ServerException(ServerStatus.ON_DELETED);

                } else {
                    throw new ServerException(ServerStatus.ON_DENIED);
                }
            }

            return entity;
        }
    }

    public String save(String entityName, Input input) {
        return save(entityName, null, input);
    }

    /**
     * 保存实体
     */
    public String save(String entityName, Object id, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (!crudSupply.support(id == null ? Crud.CREATE : Crud.UPDATE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = null;
        try {
            filter = id == null ? AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user) : AuthServiceUtils
                    .updatePropertyFilter(entityName, crudSupply, user);

        } catch (ServerException e) {
            JLog.log("admin", "save/" + (id == null ? entityName : (entityName + "/" + id)), input.getAddress(),
                    user == null ? null : user.getUsername(), false);
            return "admin/entity/save.denied";
        }

        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_WRITE);
        Object entity = edit(entityName, id, crudSupply, user);
        if (id != null) {
            crudSupply.evict(entity);
        }

        boolean create = id == null;
        Map<String, Object> crudRecord = create ? null : CrudUtils.crudRecord(new JoEntity(entityName, entity.getClass()), entity,
                filter);
        BinderData binderData = input.getBinderData();
        BinderResult binderResult = binderData.getBinderResult();
        binderResult.setPropertyFilter(filter);
        String[] subtables = input.getParams("!subtables");
        if (subtables != null) {
            Map<String, Object> subtableMap = new HashMap<String, Object>();
            for (String subtable : subtables) {
                subtableMap.put(subtable, null);
            }

            binderData.mapBind(subtableMap, entity);
        }

        binderResult.setValidation(true);
        Map<String, Object> dataMap = ParameterResolverBinder.getPropertyMap(input);
        if (!create) {
            dataMap.remove(crudSupply.getIdentifierName(entityName));
        }

        binderData.mapBind(dataMap, entity);
        JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
        CrudContextUtils.crud(create ? Crud.CREATE : Crud.UPDATE, true, crudRecord, joEntity, entity, user, filter, binderResult, input);
        InModel model = input.getModel();
        model.put("entity", entity);
        if (binderResult.hashErrors()) {
            model.put("errors", binderResult.getPropertyErrors());
            return "admin/entity/save.error";
        }

        String submitOption = input.getParam("!submitOption");
        if (!KernelString.isEmpty(submitOption) && entity instanceof ICrudSubmit) {
            ICrudSubmit submit = (ICrudSubmit<?>) entity;
            Enum<?> option = (Enum<?>) binderData.bind(submitOption, null, submit.classForOption());
            if (option != null) {
                String tpl = submit.submitOption(option, model);
                return KernelString.isEmpty(tpl) ? "admin/entity/save.option" : tpl;
            }
        }

        crudSupply.mergeEntity(entityName, entity, id == null);
        if (create) {
            //crudSupply.flush();
            model.put("create", true);
            model.put("id", crudSupply.getIdentifier(entityName, entity));
        }

        JLog.log("admin", "save/" + id == null ? entityName : (entityName + "/" + id), input.getAddress(), user == null ? null
                : user.getUsername(), true);
        return "admin/entity/save";
    }

    /**
     * 删除实体
     */
    public String delete(String entityName, @Param Object id, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, null);
        if (!crudSupply.support(Crud.DELETE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        AuthServiceUtils.deletePropertyFilter(entityName, crudSupply, user);
        try {
            EntityService.ME.delete(entityName, crudSupply, user, id);

        } catch (Throwable e) {
            JLog.log("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user.getUsername(),
                    false);
            return "admin/entity/delete.error";
        }

        JLog.log("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user.getUsername(), true);
        return "admin/entity/delete";
    }

    /**
     * 批量删除
     */
    @Mapping(method = InMethod.POST)
    public String delete(String entityName, @Param String[] ids, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, null);
        JiUserBase user = SecurityService.ME.getUserBase(input);
        AuthServiceUtils.deletePropertyFilter(entityName, crudSupply, user);
        List<JLog> logs = new ArrayList<JLog>(ids.length);
        try {
            EntityService.ME.delete(entityName, crudSupply, user, ids);

        } catch (Throwable e) {
            for (String id : ids) {
                logs.add(new JLog("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user
                        .getUsername(), false));
            }

            JLog.logs(logs);
            return "admin/entity/delete.error";
        }

        for (String id : ids) {
            logs.add(new JLog("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user
                    .getUsername(), true));
        }

        JLog.logs(logs);
        String _mapped = input.getParam("@mapped");
        if (!KernelString.isEmpty(_mapped)) {
            input.getModel().put("_mapped", _mapped);
        }

        return "admin/entity/delete";
    }

    public String deletes(String entityName, @Param String ids, Input input) {
        return delete(entityName, HelperString.split(ids, ','), input);
    }

    public String deleteJson(String entityName, @Param String ids, Input input) throws IOException {
        return delete(entityName, (String[]) HelperJson.decode(ids, String[].class), input);
    }

    /**
     * 导出Excel
     */
    @Body
    public void export(String entityName, @Nullable @Param String[] ids, Input input, HttpServletResponse response)
            throws IOException {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (crudSupply instanceof CrudSupply) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        List<Object> entities = ids == null ? EntityService.ME.list(entityName, crudSupply, user, null,
                InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), filter, null, input),
                InputServiceUtils.getOrderQueue(entityName, input), null) : EntityService.ME.list(entityName, crudSupply, user, null, ids);
        HSSFWorkbook workbook = XlsUtils.getWorkbook(entities, XlsUtils.XLS_BASE);
        response.addHeader("Content-Disposition", "attachment;filename=" + entityName + ".xls");
        workbook.write(response.getOutputStream());
    }

    @Body
    public void exportId(String entityName, @Param String id, Input input, HttpServletResponse response)
            throws IOException {
        export(entityName, new String[]{id}, input, response);
    }

    @Body
    public void exportJson(String entityName, @Nullable @Param String ids, Input input, HttpServletResponse response)
            throws IOException {
        export(entityName, (String[]) HelperJson.decode(ids, String[].class), input, response);
    }

    /**
     * 导入Excel
     */
    public String importXls(String entityName, @Param FileItem xls, Input input) throws IOException {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (crudSupply instanceof CrudSupply) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);
        if (!xls.getName().toLowerCase().endsWith(".xls")) {
            return "admin/entity/import.error";
        }

        List<?> entities;
        InModel model = input.getModel();
        try {
            model.put("xls", 1);
            HSSFWorkbook workbook = new HSSFWorkbook(xls.getInputStream());
            if (!XlsAccessorUtils.isHead(workbook, crudSupply.getEntityClass(entityName))) {
                model.put("xls", 2);
                return "admin/entity/import.error";
            }

            entities = XlsUtils.getXlsList(workbook, null, crudSupply.getEntityClass(entityName),
                    XlsUtils.XLS_BASE, false);

        } catch (Exception e) {
            return "admin/entity/import.error";
        }

        try {
            EntityService.ME.merge(entityName, crudSupply, user, entities, filter);
            JLog.log("admin", "importXls/" + entityName, input.getAddress(), user == null ? null : user.getUsername(), true);
            return "admin/entity/import";

        } catch (ServerException e) {
            model.put("e", e);
            model.put("message", e.getExceptionData());
            return "admin/entity/error";
        }
    }

    /**
     * 关联实体
     */
    public String mapped(String entityName, String id, String field, Input input) {
        edit(entityName, id, null, input);
        return "admin/entity/mapped/" + entityName + '.' + field;
    }

    /**
     * 选择授权
     */
    private void suggest(String entityName, ICrudSupply crudSupply, Input input) {
        if (crudSupply instanceof CrudSupply || !(input instanceof InputRequest)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        if (((InputRequest) input).getSession(EntityStatics.suggest(entityName)) == null) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }
    }

    /**
     * 弹出列表
     */
    public void suggest(String entityName, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        suggest(entityName, crudSupply, input);
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        input.getModel().put("entities", EntityStatics.suggest(entityName, InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), null, null, input), input));
    }

    public void lookup(String entityName, Input input) {
        lookup(entityName, null, input);
    }

    /**
     * 查找页面
     */
    @Mapping(method = InMethod.POST)
    public void lookup(String entityName, @Binder JdbcPage jdbcPage, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        suggest(entityName, crudSupply, input);
        JdbcCondition jdbcCondition = AccessServiceUtils.suggestCondition(entityName, SecurityService.ME.getUserBase(input),
                InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), null, null, input));
        jdbcPage = InputServiceUtils.getJdbcPage(entityName, jdbcPage, input);
        InModel model = input.getModel();
        model.put("page", jdbcPage);
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        model.put("entities", crudSupply.list(entityName, jdbcCondition, InputServiceUtils.getOrderQueue(entityName, input), jdbcPage));
    }

    /**
     * 上传图片
     */
    @MaPermission(RichCrudFactory.UPLOAD)
    public void upload(InputRequest inputRequest) throws IOException, FileUploadException {
        inputRequest.getModel().put("paths", UploadCrudFactory.ME.uploads(SecurityService.ME.getUserBase(inputRequest), inputRequest.getRequest()));
    }

    /**
     * 文件列表
     */
    public void filemanager(@Nullable @Param String path, @Nullable @Param String order, Input input) {
        input.getModel().put("path", path);
        input.getModel().put("files", UploadCrudFactory.ME.list(path, order));
    }

    /**
     * UE编辑器后端支持
     */
    public String ue(InputRequest inputRequest) {
        String action = inputRequest.getParam("action");
        if (!KernelString.isEmpty(action)) {
            if (action.equals("config")) {
                return "admin/ue.config";
            }
        }

        return "ue.error";
    }
}
