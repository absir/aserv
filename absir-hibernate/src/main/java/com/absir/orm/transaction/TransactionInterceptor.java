/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-3 下午3:30:15
 */
package com.absir.orm.transaction;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopProxyHandler;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class TransactionInterceptor extends AopInterceptorAbstract<TransactionManager> {

    @Override
    public Object before(Object proxy, Iterator<AopInterceptor> iterator, TransactionManager interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        interceptor.open();
        return AopProxyHandler.VOID;
    }

    @Override
    public Object after(Object proxy, Object returnValue, TransactionManager interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
        e = interceptor.close(e);
        if (e != null) {
            throw e;
        }

        return returnValue;
    }
}
