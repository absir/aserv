/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
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

/**
 * @author absir
 * 
 */
public class UserCrudFactory implements ICrudFactory, ICrudProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud.
	 * CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudFactory#getProcessor(com.absir.aserv.support
	 * .entity.value.JoEntity, com.absir.aserv.support.developer.JCrudField)
	 */
	@Override
	public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
		return this;
	}
}
