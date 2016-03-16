/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-8 下午3:48:05
 */
package com.absir.aserv.crud;

import com.absir.aserv.crud.CrudHandler.CrudInvoker;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

public abstract class CrudPropertyReference {

    protected CrudProperty crudProperty;

    protected Crud[] cruds;

    protected CrudEntity valueCrudEntity;

    public CrudProperty getCrudProperty() {
        return crudProperty;
    }

    public Crud[] getCruds() {
        return cruds;
    }

    public CrudEntity getValueCrudEntity() {
        return valueCrudEntity;
    }

    protected abstract void crud(Object entity, CrudInvoker crudHandler);
}
