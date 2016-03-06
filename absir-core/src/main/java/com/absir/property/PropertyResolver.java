/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 下午12:46:01
 */
package com.absir.property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.absir.property.value.PropertyInfo;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes" })
public interface PropertyResolver<O extends PropertyObject> {

	/**
	 * @param property
	 * @param field
	 * @return
	 */
	public O getPropertyObject(O propertyObject, Field field);

	/**
	 * @param property
	 * @param method
	 * @return
	 */
	public O getPropertyObjectGetter(O propertyObject, Method method);

	/**
	 * @param propertyObject
	 * @param method
	 * @return
	 */
	public O getPropertyObjectSetter(O propertyObject, Method method);

	/**
	 * @param propertyObject
	 * @param propertyInfos
	 * @return
	 */
	public O getPropertyObject(O propertyObject, PropertyInfo[] propertyInfos);

}
