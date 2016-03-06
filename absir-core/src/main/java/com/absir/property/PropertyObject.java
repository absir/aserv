/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 下午5:30:37
 */
package com.absir.property;

/**
 * @author absir
 * 
 */
public interface PropertyObject<T> {

	/**
	 * @param name
	 * @param property
	 * @return
	 */
	public T getPropertyData(String name, Property property);
}
