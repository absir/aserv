/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-21 下午2:51:50
 */
package com.absir.aop;

import java.lang.reflect.Method;
import java.util.Iterator;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface AopInterceptor<T> {

	/**
	 * @return
	 */
	public Class<?> getInterface();

	/**
	 * @param proxyHandler
	 * @param beanObject
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public T getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args) throws Throwable;

	/**
	 * @param iterator
	 * @param interceptor
	 * @param proxyHandler
	 * @param method
	 * @param args
	 * @param methodProxy
	 * @return
	 * @throws Throwable
	 */
	public Object before(Object proxy, Iterator<AopInterceptor> iterator, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;

	/**
	 * @param proxy
	 * @param returnValue
	 * @param interceptor
	 * @param proxyHandler
	 * @param method
	 * @param args
	 * @param e
	 * @return
	 * @throws Throwable
	 */
	public Object after(Object proxy, Object returnValue, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable;
}
