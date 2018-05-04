/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月19日 上午10:17:04
 */
package com.absir.aserv.system.domain;

import com.absir.aserv.system.bean.value.JiOpen;

public class DCacheOpenEntity<K extends JiOpen> extends DCacheOpen<K, K> {

    public DCacheOpenEntity(Class<K> entityClass, String entityName) {
        super(entityClass, entityName);
    }

    @Override
    protected K getCacheValue(K entity) {
        return entity;
    }

}
