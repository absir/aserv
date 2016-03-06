/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-8 下午12:46:06
 */
package com.absir.binder;

import java.lang.reflect.Type;

/**
 * @author absir
 * 
 */
public interface Binder {

	/**
	 * @param obj
	 * @param name
	 * @param toType
	 * @return
	 */
	public Object to(Object obj, String name, Type toType);

	/**
	 * @param obj
	 * @param name
	 * @param toClass
	 * @return
	 */
	public Object to(Object obj, String name, Class<?> toClass);
}
