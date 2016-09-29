/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午4:08:55
 */
package com.absir.async;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopProxyHandler;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class AsyncInterceptor extends AopInterceptorAbstract<AsyncRunnable> {

    @Override
    public Object before(Object proxy, Iterator<AopInterceptor> iterator, AsyncRunnable interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        interceptor.aysnc(proxy, iterator, proxyHandler, method, args, methodProxy);
        return null;
    }
}
