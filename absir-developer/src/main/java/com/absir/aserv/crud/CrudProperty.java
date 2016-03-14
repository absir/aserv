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

/**
 * @author absir
 */
public abstract class CrudProperty {

    /**
     * type
     */
    protected Class<?> type;

    /**
     * group
     */
    protected int include;

    /**
     * exclude
     */
    protected int exclude;

    /**
     * jCrud
     */
    protected JCrud jCrud;

    /**
     * crudEntity
     */
    protected CrudEntity crudEntity;

    /**
     * keyEntity
     */
    protected JoEntity keyEntity;

    /**
     * valueEntity
     */
    protected JoEntity valueEntity;

    /**
     * crudProcessor
     */
    protected ICrudProcessor crudProcessor;

    /**
     * @return
     */
    public abstract String getName();

    /**
     * @param entity
     * @return
     */
    public abstract Object get(Object entity);

    /**
     * @param entity
     * @param propertyValue
     */
    public abstract void set(Object entity, Object propertyValue);

    /**
     * @return
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return the include
     */
    public int getInclude() {
        return include;
    }

    /**
     * @return the exclude
     */
    public int getExclude() {
        return exclude;
    }

    /**
     * @return
     */
    public Accessor getAccessor() {
        return null;
    }

    /**
     * @return
     */
    public JCrud getjCrud() {
        return jCrud;
    }

    /**
     * @return
     */
    public CrudEntity getCrudEntity() {
        return crudEntity;
    }

    /**
     * @return the keyEntity
     */
    public JoEntity getKeyEntity() {
        return keyEntity;
    }

    /**
     * @return the valueEntity
     */
    public JoEntity getValueEntity() {
        return valueEntity;
    }

    /**
     * @return the crudProcessor
     */
    public ICrudProcessor getCrudProcessor() {
        return crudProcessor;
    }
}
