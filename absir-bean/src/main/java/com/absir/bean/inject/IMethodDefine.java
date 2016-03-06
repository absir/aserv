/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-20 下午3:39:58
 */
package com.absir.bean.inject;

import java.lang.reflect.Method;

import com.absir.bean.basis.BeanDefine;

/**
 * @author absir
 * 
 */
public interface IMethodDefine<T> {

	/**
	 * @param beanType
	 * @param method
	 * @param beanDefine
	 * @return
	 */
	public T getDefine(Class<?> beanType, Method method, BeanDefine beanDefine);

	/**
	 * @param define
	 * @param beanType
	 * @param beanMethod
	 * @param method
	 * @param beanDefine
	 */
	public void setDefine(T define, Class<?> beanType, Method beanMethod, Method method, BeanDefine beanDefine);
}
