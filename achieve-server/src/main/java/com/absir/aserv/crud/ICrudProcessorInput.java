/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-24 下午1:02:16
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;

/**
 * @author absir
 * 
 */
public interface ICrudProcessorInput<T> extends ICrudProcessor {

	/**
	 * @return
	 */
	public boolean isMultipart();

	/**
	 * @param crudProperty
	 * @param errors
	 * @param handler
	 * @param user
	 * @param input
	 * @return
	 */
	public T crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input);

	/**
	 * @param crudProperty
	 * @param entity
	 * @param handler
	 * @param user
	 * @param inputBody
	 */
	public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, T inputBody);

}
