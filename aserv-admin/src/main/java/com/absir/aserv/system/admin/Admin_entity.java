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
import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.JLog;
import com.absir.aserv.system.bean.JUserRolePermissions;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.ICopy;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.EntityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.UserRolePermissionsService;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.aserv.system.service.utils.InputServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.client.helper.HelperJson;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.parameter.ParameterResolverBinder;
import com.absir.server.value.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.absir.aserv.menu.MenuContextUtils.getAdminRoute;

@SuppressWarnings("unchecked")
@Base
@Server
public class Admin_entity extends AdminServer {

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
        Class<?> entityClass = crudSupply.getEntityClass(entityName);
        model.put("entities", EntityService.ME.list(entityName, crudSupply, user, null,
                InputServiceUtils.getSearchCondition(entityName, entityClass, filter, null, input),
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
            filter.exclude("*");
            model.put("update", false);
        }

        model.put("filter", filter);
        model.put("insert", AuthServiceUtils.insertPermission(crudSupply, entityName, user));
        model.put("delete", AuthServiceUtils.deletePermission(crudSupply, entityName, user));
        model.put("create", id == null && crudSupply.support(Crud.COMPLETE));
        JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
        model.put("multipart", CrudContextUtils.isMultipart(joEntity));
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        Object entity = edit(entityName, id, crudSupply, user);
        String identifierName = crudSupply.getIdentifierName(entityName);
        Object identifier = KernelString.isEmpty(identifierName) ? null : crudSupply.getIdentifier(entityName, entity);
        if (xls != null) {
            if (!xls.getName().toLowerCase().endsWith(".xls")) {
                model.put("xls", 1);

            } else {
                try {
                    HSSFWorkbook workbook = new HSSFWorkbook(xls.getInputStream());
                    if (!XlsAccessorUtils.isHead(workbook, crudSupply.getEntityClass(entityName))) {
                        model.put("xls", 2);

                    }

                    List<?> entities = XlsUtils.getXlsList(workbook, null, crudSupply.getEntityClass(entityName),
                            XlsUtils.XLS_BASE, false);
                    if (entities.size() > 0) {
                        KernelObject.clone(entities.get(0), entity);
                    }

                } catch (Exception e) {
                    model.put("xls", 3);
                }
            }
        }

        BinderData binderData = input.getBinderData();
        binderData.setLangMessage(input);
        BinderResult binderResult = binderData.getBinderResult();
        binderResult.setValidation(true);
        binderResult.setPropertyFilter(filter);
        Map<String, Object> dataMap = ParameterResolverBinder.getPropertyMap(input);
        if (!KernelString.isEmpty(identifierName)) {
            dataMap.put(identifierName, identifier);
        }

        binderData.mapBind(dataMap, entity);
        CrudContextUtils.crud(id == null ? Crud.CREATE : Crud.UPDATE, false, null, joEntity, entity, user, filter);
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

    public String save(String entityName, Input input) throws IOException {
        return save(entityName, null, input);
    }

    /**
     * 保存实体
     */
    public String save(String entityName, Object id, Input input) throws IOException {
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
        String identifierName = crudSupply.getIdentifierName(entityName);
        Object identifier = KernelString.isEmpty(identifierName) ? null : crudSupply.getIdentifier(entityName, entity);
        if (id != null) {
            crudSupply.evict(entity);
        }

        boolean create = id == null && crudSupply.support(Crud.COMPLETE);
        Map<String, Object> crudRecord = create ? null : CrudUtils.crudRecord(new JoEntity(entityName, entity.getClass()), entity,
                filter);
        BinderData binderData = input.getBinderData();
        binderData.setLangMessage(input);
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
        if (identifier != null && !KernelString.isEmpty(identifierName)) {
            dataMap.put(identifierName, identifier);
        }

        binderData.mapBind(dataMap, entity);
        JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
        boolean crudCreate = CrudContextUtils.crud(create ? Crud.CREATE : Crud.UPDATE, true, crudRecord, joEntity, entity, user, filter, binderResult, input);
        InModel model = input.getModel();
        model.put("entity", entity);
        if (binderResult.hashErrors()) {
            model.put("errors", binderResult.getPropertyErrors());
            return "admin/entity/save.error";
        }

        String submitOption = input.getParam("!submitOption");
        if (!KernelString.isEmpty(submitOption)) {
            if (filter != null && !filter.isMatch('@' + submitOption)) {
                JLog.log("admin", "save/" + (id == null ? entityName : (entityName + "/" + id)), input.getAddress(),
                        user == null ? null : user.getUsername(), false);
                return "admin/entity/save.denied";
            }

            if (entity instanceof ICrudSubmit) {
                ICrudSubmit submit = (ICrudSubmit<?>) entity;
                Enum<?> option = (Enum<?>) binderData.bind(submitOption, null, KernelClass.type(entity.getClass(), ICrudSubmit.TYPE_VARIABLE));
                if (option != null) {
                    String tpl = null;
                    try {
                        tpl = submit.submitOption(option, model);
                        if (model.containsKey(ICrudSubmit.MERGE_CODE)) {
                            crudSupply.mergeEntity(entityName, entity, crudCreate);
                            model.put("url", 1);
                        }

                    } catch (Throwable e) {
                        Environment.throwable(e);
                        model.put("icon", 2);
                        model.put("message", ICrudSubmit.OPTION_FAIL);
                    }

                    return KernelString.isEmpty(tpl) ? "admin/entity/save.option" : tpl;
                }
            }
        }

        crudSupply.mergeEntity(entityName, entity, crudCreate);
        crudSupply.flush();
        if (create) {
            //crudSupply.flush();
            model.put("create", true);
            id = crudSupply.getIdentifier(entityName, entity);
            model.put("id", id);
            model.put("url", getAdminRoute() + "entity/edit/" + entityName + "/" + id);
        }

        JLog.log("admin", "save/" + id == null ? entityName : (entityName + "/" + id), input.getAddress(), user == null ? null
                : user.getUsername(), true);
        return "admin/entity/save";
    }

    @Body
    public Object saveAjax(String entityName, Object id, Input input) throws IOException {
        List<String> binderPaths = new ArrayList<String>();
        input.getBinderData().setBinderPaths(binderPaths);
        save(entityName, id, input);
        Object entity = input.getModel().get("entity");
        Map<String, Object> data = new HashMap<String, Object>();
        for (String binderPath : binderPaths) {
            data.put(binderPath, UtilAccessor.get(entity, binderPath));
        }

        return data;
    }

    //@Body
    public Object saveAjaxSubmit(String entityName, Object id, String name, Input input) throws IOException {
        return save(entityName, id, input);
    }

    public Object saveJson(String entityName, @Param String ids, Input input) throws IOException {
        String[] _ids;
        try {
            _ids = (String[]) HelperJson.decode(ids, String[].class);

        } catch (Exception e) {
            throw new ServerException(ServerStatus.IN_404);
        }

        List<String> binderPaths = new ArrayList<String>();
        input.getBinderData().setBinderPaths(binderPaths);
        Map<String, Object> dataMap = ParameterResolverBinder.getPropertyMap(input);
        dataMap.remove("ids");

        //TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        for (String id : _ids) {
            save(entityName, id, input);
        }

        input.getModel().remove("create");
        input.getModel().remove("id");
        input.getModel().remove("url");

        input.getModel().put("url", 1);

        return "admin/entity/save.option";
    }

    public Object copyJson(String entityName, @Param String ids, Input input) throws IOException {
        String[] _ids;
        try {
            _ids = (String[]) HelperJson.decode(ids, String[].class);

        } catch (Exception e) {
            throw new ServerException(ServerStatus.IN_404);
        }

        for (String id : _ids) {
            copy(entityName, id, input);
        }

        input.getModel().remove("create");
        input.getModel().remove("id");
        input.getModel().remove("url");

        input.getModel().put("url", 1);

        return "admin/entity/save.option";
    }

    public String copy(String entityName, Object id, Input input) throws IOException {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (!crudSupply.support(Crud.CREATE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = null;
        try {
            filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);

        } catch (ServerException e) {
            JLog.log("admin", "copy/" + entityName + "/" + id, input.getAddress(),
                    user == null ? null : user.getUsername(), false);
            return "admin/entity/save.denied";
        }

        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_WRITE);
        Object old = edit(entityName, id, crudSupply, user);
        String identifierName = crudSupply.getIdentifierName(entityName);
        crudSupply.evict(old);

        ICopy iCopy = ICopy.class.isAssignableFrom(old.getClass()) ? (ICopy) old : null;
        boolean success = false;
        Object entity;
        try {
            entity = iCopy == null ? KernelObject.clone(old) : iCopy.copyFrom();
            Map<String, Object> dataMap = ParameterResolverBinder.getPropertyMap(input);
            if (!KernelString.isEmpty(identifierName)) {
                dataMap.put(identifierName, null);
            }

            Map<String, Object> crudRecord = CrudUtils.crudRecord(new JoEntity(entityName, entity.getClass()), entity, filter);
            BinderData binderData = input.getBinderData();
            binderData.setLangMessage(input);
            BinderResult binderResult = binderData.getBinderResult();
            binderResult.setPropertyFilter(filter);
            binderData.mapBind(dataMap, entity);
            JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
            boolean crudCreate = CrudContextUtils.crud(Crud.CREATE, true, crudRecord, joEntity, entity, user, filter, binderResult, input);
            InModel model = input.getModel();
            model.put("entity", entity);
            if (binderResult.hashErrors()) {
                model.put("errors", binderResult.getPropertyErrors());
                return "admin/entity/save.error";
            }

            crudSupply.mergeEntity(entityName, entity, crudCreate);
            crudSupply.flush();
            success = true;

        } finally {
            if (iCopy != null) {
                iCopy.copyDone(success);
            }
        }

        InModel model = input.getModel();
        model.put("create", true);
        Object nId = crudSupply.getIdentifier(entityName, entity);
        model.put("id", nId);
        model.put("url", getAdminRoute() + "entity/edit/" + entityName + "/" + id);

        JLog.log("admin", "copy/" + id + "-" + nId, input.getAddress(), user == null ? null
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
        String[] _ids;
        try {
            _ids = (String[]) HelperJson.decode(ids, String[].class);

        } catch (Exception e) {
            throw new ServerException(ServerStatus.IN_404);
        }

        return delete(entityName, _ids, input);
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

        entities = XlsUtils.forXlsExports(entities, 0);

        HSSFWorkbook workbook = XlsUtils.getWorkbook(entities, XlsUtils.XLS_BASE);
        response.addHeader("Content-Disposition", "attachment;filename=" + entityName + ".xls");
        workbook.write(response.getOutputStream());
    }

    @Body
    public void exportId(String entityName, @Nullable @Param String id, Input input, HttpServletResponse response)
            throws IOException {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        if (id == null && crudSupply.support(Crud.COMPLETE)) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        PropertyFilter filter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        Object entity = edit(entityName, id, crudSupply, user);
        List<Object> entities = new ArrayList<Object>();
        entities.add(entity);

        entities = XlsUtils.forXlsExports(entities, 0);

        HSSFWorkbook workbook = XlsUtils.getWorkbook(entities, XlsUtils.XLS_BASE);
        response.addHeader("Content-Disposition", "attachment;filename=" + entityName + (id == null ? "" : ("." + id)) + ".xls");
        workbook.write(response.getOutputStream());
    }

    @Body
    public void exportJson(String entityName, @Param String ids, Input input, HttpServletResponse response)
            throws IOException {
        String[] _ids;
        try {
            _ids = (String[]) HelperJson.decode(ids, String[].class);

        } catch (Exception e) {
            throw new ServerException(ServerStatus.IN_404);
        }

        export(entityName, _ids, input, response);
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

            Class<?> entityClass = crudSupply.getEntityClass(entityName);
            entities = XlsUtils.getXlsList(workbook, null, entityClass,
                    XlsUtils.XLS_BASE, true);
            entities = XlsUtils.getXlsBindList(entities, entityClass, XlsUtils.XLS_BASE, true);

        } catch (Exception e) {
            return "admin/entity/import.error";
        }

        EntityService.ME.merge(entityName, crudSupply, user, entities, filter);
        JLog.log("admin", "importXls/" + entityName, input.getAddress(), user == null ? null : user.getUsername(), true);
        return "admin/entity/import";
    }

    /**
     * 关联实体
     */
    public String mapped(String entityName, String id, String field, Input input) {
        edit(entityName, id, null, input);
        return "admin/entity/mapped/" + entityName + '.' + field;
    }

    /*
     * 角色权限（快速设置）
     */
    public String permissions(Long roleId, Input input) {
        JbUserRole userRole = (JbUserRole) BeanService.ME.get("JUserRole", roleId);
        if (userRole == null) {
            throw new ServerException(ServerStatus.IN_404);
        }

        JiUserBase user = SecurityService.ME.getUserBase(input);
        if (!Pag.rolePermissions(user)) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        InModel inModel = input.getModel();
        if (input.getMethod() == InMethod.POST) {
            Map<String, Object> propertyMap = ParameterResolverBinder.getPropertyMap(input);
            BinderData binderData = input.getBinderData();
            JUserRolePermissions rolePermissions = binderData.bind(propertyMap, null, JUserRolePermissions.class);
            UserRolePermissionsService.ME.saveUserRolePermissions(rolePermissions);
            inModel.put("icon", 1);
            return "admin/entity/save";

        } else {
            Object entity = UserRolePermissionsService.ME.getUserRolePermissions(userRole);
            inModel.put("entity", entity);
            return "admin/entity/permissions";
        }
    }

}
