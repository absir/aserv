/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-27 下午4:37:48
 */
package com.absir.aserv.configure.xls;

import java.io.Serializable;
import java.util.Collection;

import com.absir.core.kernel.KernelDyna;

/**
 * @author absir
 * 
 */
public abstract class XlsDao<T, ID extends Serializable> {

	/** idType */
	private Class<ID> idType;

	/**
	 * @param idType
	 */
	public XlsDao(Class<ID> idType) {
		this.idType = idType;
	}

	/**
	 * @return the idType
	 */
	public Class<ID> getIdType() {
		return idType;
	}

	/**
	 * @param id
	 * @return
	 */
	public abstract T get(ID id);

	/**
	 * @return
	 */
	public abstract Collection<T> getAll();

	/**
	 * @param id
	 * @return
	 */
	public T find(Object id) {
		ID identifier = KernelDyna.to(id, idType);
		return identifier == null ? null : get(identifier);
	}
}
