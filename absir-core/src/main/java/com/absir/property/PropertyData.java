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
public class PropertyData {

	/** property */
	private Property property;

	/** propertyObject */
	private Object[] propertyDatas;

	/**
	 * @param beanClass
	 * @param name
	 * @param include
	 * @param exclude
	 * @param beanName
	 * @param factoryClass
	 */
	public PropertyData(Class<?> beanClass, String name, int include, int exclude, String beanName, Class<? extends PropertyFactory> factoryClass) {
		property = new Property(beanClass, name, include, exclude, beanName, factoryClass);
		propertyDatas = new Object[PropertySupply.getSupplySize()];
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return the propertyDatas
	 */
	public Object[] getPropertyDatas() {
		return propertyDatas;
	}
}
