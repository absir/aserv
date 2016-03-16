/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-27 上午9:44:05
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.core.dyna.DynaBinder;
import com.absir.orm.value.JoEntity;

public class UserCrudFactory implements ICrudFactory, ICrudProcessor {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
        if (user != null) {
            if (crudProperty.getType().isAssignableFrom(user.getClass())) {
                crudProperty.set(entity, user);

            } else {
                crudProperty.set(entity, DynaBinder.to(user.getUserId(), crudProperty.getType()));
            }
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return this;
    }
}
