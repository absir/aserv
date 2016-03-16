/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-11 下午1:56:46
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.core.kernel.KernelDyna;
import com.absir.orm.value.JoEntity;

public class DateCrudFactory implements ICrudFactory, ICrudProcessor {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
        if (crudHandler.getCrud() != JaCrud.Crud.CREATE || KernelDyna.to(crudProperty.get(entity), long.class) <= 0) {
            crudProperty.set(entity, KernelDyna.to(System.currentTimeMillis(), crudProperty.getType()));
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return this;
    }
}
