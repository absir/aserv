/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-11 上午10:48:41
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.orm.value.JoEntity;
import com.absir.server.in.Input;

public class BeanCrudFactory implements ICrudFactory, ICrudProcessor {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user, Input input) {
        if (crudHandler.getRoot() != entity && entity instanceof ICrudBean) {
            ((ICrudBean) entity).processCrud(crudHandler.getCrud(), crudHandler, input);
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        if (joEntity.getEntityName() == null || joEntity.getEntityClass() == null) {
            return null;
        }

        return this;
    }
}
