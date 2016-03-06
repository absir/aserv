/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-12 下午4:08:01
 */
package com.absir.property;

/**
 * @author absir
 * 
 */
public interface PropertyFactory {

	/**
	 * @author absir
	 * 
	 */
	public interface Void extends PropertyFactory {

	}

	/**
	 * @param property
	 * @return
	 */
	public PropertyConvert getPropertyConvert(Property property);
}
