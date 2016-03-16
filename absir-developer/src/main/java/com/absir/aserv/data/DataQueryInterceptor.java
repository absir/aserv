/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午5:12:29
 */
package com.absir.aserv.data;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopProxyHandler;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class DataQueryInterceptor extends AopInterceptorAbstract<DataQueryDetached> {

    @Override
    public Object before(Object proxy, Iterator<AopInterceptor> iterator, DataQueryDetached interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        return interceptor.invoke(args);
    }
}
