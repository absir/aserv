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
public interface PropertyConvert {

	/**
	 * @param propertyValue
	 * @return
	 */
	public Object getValue(Object propertyValue);

	/**
	 * @param value
	 * @param beanName
	 * @return
	 */
	public Object getPropertyValue(Object value, String beanName);

}
