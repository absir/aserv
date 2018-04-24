/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-9 上午11:38:52
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.service.CrudService;
import com.absir.binder.BinderSupply;
import com.absir.binder.BinderUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.property.PropertyData;
import com.absir.property.PropertyHolder;
import com.absir.property.PropertyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class CrudServiceUtils {

    public static Class<?> getEntityClass(String entityName) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        return crudSupply == null ? null : crudSupply.getEntityClass(entityName);
    }

    public static Object find(String entityName, Object id, JdbcCondition jdbcCondition) {
        return find(CrudService.ME.getCrudSupply(entityName), entityName, id, jdbcCondition);
    }

    public static Object find(ICrudSupply crudSupply, String entityName, Object id, JdbcCondition jdbcCondition) {
        return crudSupply.get(entityName, DynaBinder.to(id, crudSupply.getIdentifierType(entityName)), jdbcCondition);
    }

    public static List list(String entityName, Object[] ids, JdbcCondition jdbcCondition) {
        return list(entityName, ids, CrudService.ME.getCrudSupply(entityName), jdbcCondition);
    }

    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        return CrudService.ME.getCrudSupply(entityName).list(entityName, jdbcCondition, queue, firstResult, maxResults);
    }

    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        return CrudService.ME.getCrudSupply(entityName).list(entityName, jdbcCondition, queue, jdbcPage);
    }

    public static void merge(String entityName, Map<String, Object> crudRecord, Object entity, boolean create, JiUserBase user,
                             PropertyFilter filter) {
        CrudService.ME.merge(entityName, crudRecord, entity, CrudService.ME.getCrudSupply(entityName), create, user, filter);
    }

    public static Object delete(String entityName, Object id, JdbcCondition jdbcCondition, JiUserBase user) {
        return delete(CrudService.ME.getCrudSupply(entityName), entityName, id, jdbcCondition, user);
    }

    public static Object delete(ICrudSupply crudSupply, String entityName, Object id, JdbcCondition jdbcCondition, JiUserBase user) {
        return CrudService.ME.delete(entityName, DynaBinder.to(id, crudSupply.getIdentifierType(entityName)), crudSupply,
                jdbcCondition, user);
    }

    public static JdbcCondition ids(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition) {
        int length = ids == null ? 0 : ids.length;
        if (length == 0) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        Class<? extends Serializable> identifierType = crudSupply.getIdentifierType(entityName);
        Object[] identifiers = new Object[length];
        for (int i = 0; i < length; i++) {
            identifiers[i] = DynaBinderUtils.getParamObject(ids[i], identifierType);
        }

        List<Object> conditions = jdbcCondition.getConditions();
        if (KernelClass.isCustomClass(identifierType)) {
            String conditionStr = JdbcCondition.ALIAS + '.' + crudSupply.getIdentifierName(entityName) + '.';
            BinderSupply binderSupply = BinderUtils.getBinderSupply();
            PropertyHolder propertyHolder = PropertyUtils.getPropertyMap(identifierType, binderSupply);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, PropertyData> entry : propertyHolder.getNameMapPropertyData().entrySet()) {
                PropertyData propertyData = entry.getValue();
                if (propertyData.getProperty().getAllow() == 0) {
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append('(');

                    } else {
                        stringBuilder.append(" AND ");
                    }

                    stringBuilder.append(conditionStr);
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append(" = ?");
                }
            }

            stringBuilder.append(')');
            conditionStr = stringBuilder.toString();
            stringBuilder.setLength(0);
            List<Object> params = new ArrayList<Object>();
            for (int i = 0; i < length; i++) {
                Object identifier = identifiers[i];
                if (identifier != null) {
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append('(');
                    } else {
                        stringBuilder.append(" OR ");
                    }

                    stringBuilder.append(conditionStr);
                    for (Map.Entry<String, PropertyData> entry : propertyHolder.getNameMapPropertyData().entrySet()) {
                        PropertyData propertyData = entry.getValue();
                        if (propertyData.getProperty().getAllow() == 0) {
                            params.add(propertyData.getProperty().getAccessor().get(identifier));
                        }
                    }
                }
            }

            stringBuilder.append(')');
            conditions.add(stringBuilder.toString());
            conditions.add(params);

        } else {
            conditions.add(0, JdbcCondition.ALIAS + "." + crudSupply.getIdentifierName(entityName) + " IN (?)");
            conditions.add(1, identifiers);
        }

        return jdbcCondition;
    }

    public static List list(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition) {
        return crudSupply.list(entityName, ids(entityName, ids, crudSupply, jdbcCondition), null, 0, 0);
    }

    public static List delete(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user) {
        return CrudService.ME.delete(entityName, crudSupply, ids(entityName, ids, crudSupply, jdbcCondition), user);
    }
}
