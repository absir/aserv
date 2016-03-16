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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class AopInterceptorAbstract<T> implements AopInterceptor<T> {

    protected Map<Method, T> methodMapInterceptor = new HashMap<Method, T>();

    @Override
    public Class<?> getInterface() {
        return null;
    }

    public Map<Method, T> getMethodMapInterceptor() {
        return methodMapInterceptor;
    }

    public boolean setunmodifiableMethodMapInterceptor() {
        if (methodMapInterceptor == null || methodMapInterceptor.isEmpty()) {
            return false;
        }

        methodMapInterceptor = Collections.unmodifiableMap(methodMapInterceptor);
        return true;
    }

    @Override
    public T getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args) throws Throwable {
        return methodMapInterceptor.get(method);
    }

    @Override
    public Object before(Object proxy, Iterator<AopInterceptor> iterator, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return AopProxyHandler.VOID;
    }

    @Override
    public Object after(Object proxy, Object returnValue, T interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
        return returnValue;
    }
}
