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
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelLang.PropertyFilter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public abstract class CrudServiceUtils {

    /**
     * @param entityName
     * @return
     */
    public static Class<?> getEntityClass(String entityName) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        return crudSupply == null ? null : crudSupply.getEntityClass(entityName);
    }

    /**
     * @param entityName
     * @param entity
     * @param create
     * @return
     */
    public static Object identifier(String entityName, Object entity, boolean create) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        if (crudSupply != null) {
            Object identifier = create ? null : crudSupply.getIdentifier(entityName, entity);
            if (identifier == null) {
                crudSupply.mergeEntity(entityName, entity, create);
                //crudSupply.flush();
                return crudSupply.getIdentifier(entityName, entity);
            }
        }

        return null;
    }

    /**
     * @param entityName
     * @param id
     * @param jdbcCondition
     * @return
     */
    public static Object find(String entityName, Object id, JdbcCondition jdbcCondition) {
        return find(CrudService.ME.getCrudSupply(entityName), entityName, id, jdbcCondition);
    }

    /**
     * @param crudSupply
     * @param entityName
     * @param id
     * @param jdbcCondition
     * @return
     */
    public static Object find(ICrudSupply crudSupply, String entityName, Object id, JdbcCondition jdbcCondition) {
        return crudSupply.get(entityName, DynaBinder.to(id, crudSupply.getIdentifierType(entityName)), jdbcCondition);
    }

    /**
     * @param entityName
     * @param ids
     * @param jdbcCondition
     * @return
     */
    public static List list(String entityName, Object[] ids, JdbcCondition jdbcCondition) {
        return list(entityName, ids, CrudService.ME.getCrudSupply(entityName), jdbcCondition);
    }

    /**
     * @param entityName
     * @param jdbcCondition
     * @param queue
     * @param firstResult
     * @param maxResults
     * @return
     */
    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        return CrudService.ME.getCrudSupply(entityName).list(entityName, jdbcCondition, queue, firstResult, maxResults);
    }

    /**
     * @param entityName
     * @param jdbcCondition
     * @param queue
     * @param jdbcPage
     * @return
     */
    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        return CrudService.ME.getCrudSupply(entityName).list(entityName, jdbcCondition, queue, jdbcPage);
    }

    /**
     * @param entityName
     * @param crudRecord
     * @param entity
     * @param create
     * @param user
     * @param filter
     */
    public static void merge(String entityName, Map<String, Object> crudRecord, Object entity, boolean create, JiUserBase user,
                             PropertyFilter filter) {
        CrudService.ME.merge(entityName, crudRecord, entity, CrudService.ME.getCrudSupply(entityName), create, user, filter);
    }

    /**
     * @param entityName
     * @param id
     * @param jdbcCondition
     * @param user
     * @return
     */
    public static Object delete(String entityName, Object id, JdbcCondition jdbcCondition, JiUserBase user) {
        return delete(CrudService.ME.getCrudSupply(entityName), entityName, id, jdbcCondition, user);
    }

    /**
     * @param crudSupply
     * @param entityName
     * @param id
     * @param jdbcCondition
     * @param user
     * @return
     */
    public static Object delete(ICrudSupply crudSupply, String entityName, Object id, JdbcCondition jdbcCondition, JiUserBase user) {
        return CrudService.ME.delete(entityName, DynaBinder.to(id, crudSupply.getIdentifierType(entityName)), crudSupply,
                jdbcCondition, user);
    }

    /**
     * @param entityName
     * @param ids
     * @param crudSupply
     * @param jdbcCondition
     * @return
     */
    public static JdbcCondition ids(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition) {
        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        Class<? extends Serializable> identifierType = crudSupply.getIdentifierType(entityName);
        int length = ids.length;
        Object[] identifiers = new Object[length];
        for (int i = 0; i < length; i++) {
            identifiers[i] = DynaBinderUtils.getParamObject(ids[i], identifierType);
        }

        jdbcCondition.getConditions().add(0, JdbcCondition.ALIAS + "." + crudSupply.getIdentifierName(entityName) + " IN (?)");
        jdbcCondition.getConditions().add(1, identifiers);
        return jdbcCondition;
    }

    /**
     * @param entityName
     * @param ids
     * @param crudSupply
     * @param jdbcCondition
     * @return
     */
    public static List list(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition) {
        return crudSupply.list(entityName, ids(entityName, ids, crudSupply, jdbcCondition), null, 0, 0);
    }

    /**
     * @param entityName
     * @param ids
     * @param crudSupply
     * @param jdbcCondition
     * @param user
     * @return
     */
    public static List delete(String entityName, Object[] ids, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user) {
        return CrudService.ME.delete(entityName, crudSupply, ids(entityName, ids, crudSupply, jdbcCondition), user);
    }
}
