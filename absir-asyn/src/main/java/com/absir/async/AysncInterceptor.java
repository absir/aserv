/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-3 下午4:08:55
 */
package com.absir.async;

import java.lang.reflect.Method;
import java.util.Iterator;

import net.sf.cglib.proxy.MethodProxy;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopProxyHandler;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class AysncInterceptor extends AopInterceptorAbstract<AysncRunable> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopInterceptorAbstract#before(java.lang.Object,
	 * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
	 * java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object before(Object proxy, Iterator<AopInterceptor> iterator, AysncRunable interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
			throws Throwable {
		interceptor.aysnc(proxy, iterator, proxyHandler, method, args, methodProxy);
		return null;
	}
}
