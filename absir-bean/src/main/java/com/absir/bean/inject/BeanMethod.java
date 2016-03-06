/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-23 下午3:42:27
 */
package com.absir.bean.inject;

import java.lang.reflect.Method;

import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public class BeanMethod {

	/** beanType */
	Class<?> beanType;

	/** method */
	Method method;

	/**
	 * 
	 */
	public BeanMethod(Class<?> beanType, Method method) {
		this.beanType = beanType;
		this.method = method;
	}

	/**
	 * @return the beanType
	 */
	public Class<?> getBeanType() {
		return beanType;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(beanType) + KernelObject.hashCode(method);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof BeanMethod) {
			return KernelObject.equals(beanType, ((BeanMethod) obj).beanType) && KernelObject.equals(method, ((BeanMethod) obj).method);
		}

		return false;
	}

}
