/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-6 下午1:34:05
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.service.CrudService;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.orm.value.JoEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 *
 */
@SuppressWarnings({"rawtypes"})
@Bean
public class CrudServiceImpl implements CrudService {

    /**
     * crudSupports
     */
    @Inject(type = InjectType.Selectable)
    private ICrudSupply[] crudSupplies;

    /**
     * entityNameMap
     */
    private Map<String, ICrudSupply> entityNameMapCrudSupply = new HashMap<String, ICrudSupply>();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.service.CrudService#getCrudSupport(java.lang
     * .String)
     */
    @Override
    public ICrudSupply getCrudSupply(String entityName) {
        ICrudSupply crudSupply = entityNameMapCrudSupply.get(entityName);
        if (crudSupply == null && crudSupplies != null) {
            for (ICrudSupply supply : crudSupplies) {
                if (supply.getEntityClass(entityName) != null) {
                    crudSupply = supply;
                    entityNameMapCrudSupply.put(entityName, crudSupply);
                }
            }
        }

        return crudSupply;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.service.CrudService#merge(java.lang.String,
     * java.util.Map, java.lang.Object, com.absir.aserv.crud.ICrudSupply,
     * boolean, com.absir.aserv.system.bean.proxy.JiUserBase,
     * com.absir.core.kernel.KernelLang.PropertyFilter)
     */
    @Override
    public void merge(String entityName, Map<String, Object> crudRecord, Object entity, ICrudSupply crudSupply, boolean create, JiUserBase user, PropertyFilter filter) {
        CrudUtils.crud(create ? Crud.CREATE : Crud.UPDATE, crudRecord, new JoEntity(entityName, entity.getClass()), entity, filter, user);
        crudSupply.mergeEntity(entityName, entity, create);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.service.CrudService#delete(java.lang.String,
     * java.io.Serializable, com.absir.aserv.crud.ICrudSupply,
     * com.absir.aserv.jdbc.JdbcCondition,
     * com.absir.aserv.system.bean.proxy.JiUserBase)
     */
    @Override
    public Object delete(String entityName, Serializable id, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user) {
        Object entity = crudSupply.get(entityName, id, jdbcCondition);
        if (entity == null) {
            return null;
        }

        CrudUtils.crud(Crud.DELETE, null, new JoEntity(entityName, entity.getClass()), entity, null, user);
        crudSupply.deleteEntity(entityName, entity);
        return entity;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.service.CrudService#delete(java.lang.String,
     * com.absir.aserv.crud.ICrudSupply, com.absir.aserv.jdbc.JdbcCondition,
     * com.absir.aserv.system.bean.proxy.JiUserBase)
     */
    @Override
    public List delete(String entityName, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user) {
        JoEntity joEntity = null;
        List entities = crudSupply.list(entityName, jdbcCondition, null, 0, 0);
        for (Object entity : entities) {
            if (entity != null) {
                if (joEntity == null) {
                    joEntity = new JoEntity(entityName, entity.getClass());
                }

                CrudUtils.crud(Crud.DELETE, null, new JoEntity(entityName, entity.getClass()), entity, null, user);
                crudSupply.deleteEntity(entityName, entity);
            }
        }

        return entities;
    }
}
