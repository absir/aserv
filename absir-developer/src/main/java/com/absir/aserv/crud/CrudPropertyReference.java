/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-8 下午3:48:05
 */
package com.absir.aserv.crud;

import com.absir.aserv.crud.CrudHandler.CrudInvoker;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

/**
 * @author absir
 * 
 */
public abstract class CrudPropertyReference {

	/** crudProperty */
	protected CrudProperty crudProperty;

	/** cruds */
	protected Crud[] cruds;

	/** valueCrudEntity */
	protected CrudEntity valueCrudEntity;

	/**
	 * @return
	 */
	public CrudProperty getCrudProperty() {
		return crudProperty;
	}

	/**
	 * @return the cruds
	 */
	public Crud[] getCruds() {
		return cruds;
	}

	/**
	 * @return the valueCrudEntity
	 */
	public CrudEntity getValueCrudEntity() {
		return valueCrudEntity;
	}

	/**
	 * @param entity
	 * @param crudInvoker
	 */
	protected abstract void crud(Object entity, CrudInvoker crudHandler);
}
