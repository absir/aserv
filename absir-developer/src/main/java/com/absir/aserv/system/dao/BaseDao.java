/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-27 上午9:54:40
 */
package com.absir.aserv.system.dao;

import java.io.Serializable;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

/**
 * @author absir
 * 
 */
public interface BaseDao<T, ID extends Serializable> {

	/**
	 * @param crud
	 * @param property
	 * @param crudHandler
	 * @param entity
	 */
	public void crud(Crud crud, CrudProperty property, CrudHandler crudHandler, T entity);

}
