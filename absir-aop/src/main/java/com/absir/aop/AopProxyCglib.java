/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author absir
 * 
 */
public class AopProxyCglib extends AopProxyHandler implements MethodInterceptor {

	/**
	 * @param beanType
	 * @param beanObject
	 */
	public AopProxyCglib(Class<?> beanType, Object beanObject) {
		super(beanType, beanObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopProxyHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		if (beanObject == null) {
			return methodProxy.invokeSuper(proxy, args);

		} else {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}

			return method.invoke(beanObject, args);
		}
	}
}
