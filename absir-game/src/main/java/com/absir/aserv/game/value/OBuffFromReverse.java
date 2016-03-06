/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-6 上午10:01:15
 */
package com.absir.aserv.game.value;

import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class OBuffFromReverse<T, O extends OObject> extends OBuffReverse<O> implements IBuffFrom<T> {

	/** formType */
	private Class<T> formType;

	/**
	 * 
	 */
	public OBuffFromReverse() {
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
