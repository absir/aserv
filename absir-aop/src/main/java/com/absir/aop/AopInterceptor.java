/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 下午2:51:50
 */
package com.absir.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public interface AopInterceptor<T> {

    public Class<?> getInterface();

    public T getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args) throws Throwable;

    public Object before(Object proxy, Iterator<AopInterceptor> iterator, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;

    public Object after(Object proxy, Object returnValue, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable;
}
