/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-7 下午5:44:54
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.inject.InjectRetain;

/**
 * @author absir
 * 
 */
public interface ICrudProcessor extends InjectRetain {

	/**
	 * @param crudProperty
	 * @param entity
	 * @param handler
	 * @param user
	 */
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user);
}
