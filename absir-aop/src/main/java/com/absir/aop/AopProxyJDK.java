/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author absir
 * 
 */
public class AopProxyJDK extends AopProxyHandler implements InvocationHandler {

	/**
	 * @param beanType
	 * @param beanObject
	 */
	public AopProxyJDK(Class<?> beanType, Object beanObject) {
		super(beanType, beanObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return intercept(proxy, method, args, null);
	}
}
