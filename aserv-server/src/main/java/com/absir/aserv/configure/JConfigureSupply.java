/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-24 上午10:42:15
 */
package com.absir.aserv.configure;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.bean.basis.Basis;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;

@SuppressWarnings("unchecked")
@Bean
@Basis
@MaSupply(folder = "系统配置", name = "配置", method = "edit", icon = "cogs")
public class JConfigureSupply extends CrudSupply<JConfigureBase> {

    public static final JConfigureSupply ME = BeanFactoryUtils.get(JConfigureSupply.class);

    @Override
    protected void put(Class<?> type, Class<?> beanType) {
        JConfigureUtils.put((Class<? extends JConfigureBase>) type, (Class<? extends JConfigureBase>) beanType);
    }

    @Override
    public Object create(String entityName) {
        return JConfigureUtils.createCrudConfigure(getEntityClass(entityName));
    }

    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {
        JConfigureBase configure = (JConfigureBase) entity;
        configure.merge();
        Class<? extends JConfigureBase> configureClass = getEntityClass(entityName);
        JConfigureBase configureEntity = JConfigureUtils.findConfigure(configureClass);
        if (configureEntity != null) {
            JConfigureUtils.cloneConfigureBase(configure, configureEntity);
        }
    }

    @Transaction
    @Override
    public void deleteEntity(String entityName, Object entity) {
        JConfigureBase configureBase = (JConfigureBase) entity;
        if (configureBase.isDeleteClear()) {
            configureBase.delete();
        }
    }
}
