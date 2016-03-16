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
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BaseDao;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.orm.value.JoEntity;

@SuppressWarnings("unchecked")
public class DaoCrudFactory implements ICrudFactory, ICrudProcessor {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
        BaseDao<Object, ?> baseDao = (BaseDao<Object, ?>) BeanDao.getBaseDao(entity.getClass());
        if (baseDao == null) {
            return;
        }

        baseDao.crud(crudHandler.getCrud(), crudProperty, crudHandler, entity);
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        if (joEntity.getEntityName() == null || joEntity.getEntityClass() == null) {
            return null;
        }

        return this;
    }
}
