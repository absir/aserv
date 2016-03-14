/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午5:51:26
 */
package com.absir.aserv.system.domain;

import com.absir.core.base.IBase;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
public class DCacheEntity<V extends IBase> extends DCache<V, V> {

    /**
     * @param entityClass
     * @param entityName
     */
    public DCacheEntity(Class<V> entityClass, String entityName) {
        super(entityClass, entityName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.domain.DCache#getCacheValue(com.absir.aserv.
     * system.bean.base.JbBase)
     */
    @Override
    protected V getCacheValue(V entity) {
        return entity;
    }

}
