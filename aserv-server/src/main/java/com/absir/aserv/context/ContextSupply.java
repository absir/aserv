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

@SuppressWarnings({"rawtypes", "unchecked"})
@Bean
@Basis
@MaSupply(folder = "功能管理", icon = "briefcase")
public class ContextSupply extends CrudSupply<Context> {

    private Map<String, Class<Serializable>> entityNameMapIdType = new HashMap<String, Class<Serializable>>();

    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        List<BeanDefine> beanDefines = super.getBeanDefines(beanFactory, beanType);
        if (beanDefines != null) {
            entityNameMapIdType.put(beanType.getSimpleName(), Context.getIdType((Class<? extends Context>) beanType));
        }

        return beanDefines;
    }

    @Override
    public boolean support(Crud crud) {
        return crud != Crud.CREATE;
    }

    @Override
    public String getIdentifierName(String entityName) {
        return "id";
    }

    @Override
    public Class<? extends Serializable> getIdentifierType(String entityName) {
        return entityNameMapIdType.get(entityName);
    }

    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return ((IBase) entity).getId();
    }

    @Override
    public Collection findAll(String entityName) {
        Map<Serializable, Context> contextMap = ContextUtils.getContextFactory().findContextMap(getEntityClass(entityName));
        return contextMap == null ? null : contextMap.values();
    }

    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        return ContextUtils.findContext(getEntityClass(entityName), id);
    }

    @Override
    public Object create(String entityName) {
        return null;
    }

    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {
    }

    @Override
    public void deleteEntity(String entityName, Object entity) {
        ContextUtils.getContextFactory().clearContext((Context) entity, getEntityClass(entityName), true);
    }
}
