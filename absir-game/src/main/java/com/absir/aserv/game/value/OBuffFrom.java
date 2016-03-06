/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 下午5:17:18
 */
package com.absir.aserv.game.value;

import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class OBuffFrom<T, O extends OObject> extends OBuff<O> implements IBuffFrom<T> {

	/** formType */
	private Class<T> formType;

	/**
	 * 
	 */
	public OBuffFrom() {
		formType = KernelClass.argumentClass(formType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.IBuffFrom#supportsFrom(java.lang.Object)
	 */
	@Override
	public boolean supportsFrom(Object from) {
		return from == null ? isFromNullable() : formType.isAssignableFrom(formType.getClass());
	}

	/**
	 * @return
	 */
	public boolean isFromNullable() {
		return false;
	}
}
