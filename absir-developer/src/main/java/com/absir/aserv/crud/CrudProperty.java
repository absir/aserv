/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-8 下午4:49:27
 */
package com.absir.aserv.crud;

import com.absir.aserv.support.developer.JCrud;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;

public abstract class CrudProperty {

    protected Class<?> type;

    protected int include;

    protected int exclude;

    protected JCrud jCrud;

    protected CrudEntity crudEntity;

    protected JoEntity keyEntity;

    protected JoEntity valueEntity;

    protected ICrudProcessor crudProcessor;

    public abstract String getName();

    public abstract Object get(Object entity);

    public abstract void set(Object entity, Object propertyValue);

    public Class<?> getType() {
        return type;
    }

    public int getInclude() {
        return include;
    }

    public int getExclude() {
        return exclude;
    }

    public Accessor getAccessor() {
        return null;
    }

    public JCrud getjCrud() {
        return jCrud;
    }

    public CrudEntity getCrudEntity() {
        return crudEntity;
    }

    public JoEntity getKeyEntity() {
        return keyEntity;
    }

    public JoEntity getValueEntity() {
        return valueEntity;
    }

    public ICrudProcessor getCrudProcessor() {
        return crudProcessor;
    }
}
