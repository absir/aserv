/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-24 下午1:05:52
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.proxy.JiUserBase;

public abstract class CrudProcessorInput<T> implements ICrudProcessorInput<T> {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user) {
    }
}
