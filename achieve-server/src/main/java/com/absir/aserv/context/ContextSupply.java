/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月15日 下午12:39:40
 */
package com.absir.aserv.context;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.Context;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.IBase;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Bean
@Basis
@MaSupply(folder = "功能管理", icon = "briefcase")
public class ContextSupply extends CrudSupply<Context> {

    /**
     * entityNameMapIdType
     */
    private Map<String, Class<Serializable>> entityNameMapIdType = new HashMap<String, Class<Serializable>>();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.config.IBeanDefineSupply#getBeanDefines(com.absir.bean
     * .core.BeanFactoryImpl, java.lang.Class)
     */
    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        List<BeanDefine> beanDefines = super.getBeanDefines(beanFactory, beanType);
        if (beanDefines != null) {
            entityNameMapIdType.put(beanType.getSimpleName(), Context.getIdType((Class<? extends Context>) beanType));
        }

        return beanDefines;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.crud.ICrudSupply#support(com.absir.aserv.system.bean
     * .value.JaCrud.Crud)
     */
    @Override
    public boolean support(Crud crud) {
        return crud != Crud.CREATE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.crud.ICrudSupply#getIdentifierName(java.lang.String)
     */
    @Override
    public String getIdentifierName(String entityName) {
        return "id";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.crud.ICrudSupply#getIdentifierType(java.lang.String)
     */
    @Override
    public Class<? extends Serializable> getIdentifierType(String entityName) {
        return entityNameMapIdType.get(entityName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.ICrudSupply#getIdentifier(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return ((IBase) entity).getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.CrudSupply#findAll(java.lang.String)
     */
    @Override
    public Collection findAll(String entityName) {
        Map<Serializable, Context> contextMap = ContextUtils.getContextFactory().findContextMap(getEntityClass(entityName));
        return contextMap == null ? null : contextMap.values();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.ICrudSupply#get(java.lang.String,
     * java.io.Serializable, com.absir.aserv.jdbc.JdbcCondition)
     */
    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        return ContextUtils.findContext(getEntityClass(entityName), id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.ICrudSupply#create(java.lang.String)
     */
    @Override
    public Object create(String entityName) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.ICrudSupply#mergeEntity(java.lang.String,
     * java.lang.Object, boolean)
     */
    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.crud.ICrudSupply#deleteEntity(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void deleteEntity(String entityName, Object entity) {
        ContextUtils.getContextFactory().clearContext((Context) entity, getEntityClass(entityName), true);
    }
}
