/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-8 下午12:46:06
 */
package com.absir.binder;

import com.absir.property.Property;
import com.absir.property.PropertyObject;

/**
 * @author absir
 * 
 */
public class BinderObject implements PropertyObject<Binder> {

	private Binder binder;

	/**
	 * @return the binder
	 */
	public Binder getBinder() {
		return binder;
	}

	/**
	 * @param binder
	 *            the binder to set
	 */
	public void setBinder(Binder binder) {
		this.binder = binder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertyObject#getPropertyData(java.lang.String,
	 * com.absir.property.Property)
	 */
	@Override
	public Binder getPropertyData(String name, Property property) {
		return binder;
	}
}
