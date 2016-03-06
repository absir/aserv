/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-21 下午2:51:50
 */
package com.absir.aop;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class AopInterceptorAbstract<T> implements AopInterceptor<T> {

	/** methodMapInterceptor */
	protected Map<Method, T> methodMapInterceptor = new HashMap<Method, T>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopInterceptor#getInterface()
	 */
	@Override
	public Class<?> getInterface() {
		return null;
	}

	/**
	 * @return the methodMapInterceptor
	 */
	public Map<Method, T> getMethodMapInterceptor() {
		return methodMapInterceptor;
	}

	/**
	 * @return
	 */
	public boolean setunmodifiableMethodMapInterceptor() {
		if (methodMapInterceptor == null || methodMapInterceptor.isEmpty()) {
			return false;
		}

		methodMapInterceptor = Collections.unmodifiableMap(methodMapInterceptor);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aop.AopInterceptor#getInterceptor(com.absir.aop.AopProxyHandler
	 * , java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public T getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args) throws Throwable {
		return methodMapInterceptor.get(method);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopInterceptor#before(java.lang.Object,
	 * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
	 * java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object before(Object proxy, Iterator<AopInterceptor> iterator, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		return AopProxyHandler.VOID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopInterceptor#after(java.lang.Object,
	 * java.lang.Object, java.lang.Object, com.absir.aop.AopProxyHandler,
	 * java.lang.reflect.Method, java.lang.Object[], java.lang.Throwable)
	 */
	@Override
	public Object after(Object proxy, Object returnValue, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
		return returnValue;
	}
}
