/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-24 下午1:05:52
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.proxy.JiUserBase;

/**
 * @author absir
 * 
 */
public abstract class CrudProcessorInput<T> implements ICrudProcessorInput<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudProcessor#crud(com.absir.aserv.crud.
	 * CrudProperty, java.lang.Object, com.absir.aserv.crud.CrudHandler,
	 * com.absir.aserv.system.bean.proxy.JiUserBase)
	 */
	@Override
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user) {
	}
}
