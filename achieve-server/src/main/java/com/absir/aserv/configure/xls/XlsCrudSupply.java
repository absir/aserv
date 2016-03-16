/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-23 上午10:57:02
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.menu.value.MaSupply;
import com.absir.bean.basis.Basis;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;

import java.io.Serializable;
import java.util.Collection;

@SuppressWarnings({"rawtypes"})
@Bean
@Basis
@MaSupply
public class XlsCrudSupply extends CrudSupply<XlsBase> {

    public static final XlsCrudSupply ME = BeanFactoryUtils.get(XlsCrudSupply.class);

    @Override
    public String getIdentifierName(String entityName) {
        return "id";
    }

    @Override
    public Class<? extends Serializable> getIdentifierType(String entityName) {
        return XlsUtils.getXlsDao(getEntityClass(entityName)).getIdType();
    }

    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return ((IBase) entity).getId();
    }

    @Override
    public Collection findAll(String entityName) {
        return XlsUtils.getXlsDao(getEntityClass(entityName)).getAll();
    }

    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        return XlsUtils.getXlsDao(getEntityClass(entityName)).get(id);
    }

    @Override
    public Object create(String entityName) {
        return KernelClass.newInstance(getEntityClass(entityName));
    }

    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {
    }

    @Override
    public void deleteEntity(String entityName, Object entity) {
    }
}
