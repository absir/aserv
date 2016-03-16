/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午5:51:26
 */
package com.absir.aserv.system.domain;

import com.absir.core.base.IBase;

@SuppressWarnings("rawtypes")
public class DCacheEntity<V extends IBase> extends DCache<V, V> {

    public DCacheEntity(Class<V> entityClass, String entityName) {
        super(entityClass, entityName);
    }

    @Override
    protected V getCacheValue(V entity) {
        return entity;
    }

}
