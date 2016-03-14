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

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class TransactionInterceptor extends AopInterceptorAbstract<TransactionManager> {

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopInterceptorAbstract#before(java.lang.Object,
     * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
     * java.lang.reflect.Method, java.lang.Object[],
     * net.sf.cglib.proxy.MethodProxy)
     */
    @Override
    public Object before(Object proxy, Iterator<AopInterceptor> iterator, TransactionManager interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        interceptor.open();
        return AopProxyHandler.VOID;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aop.AopInterceptorAbstract#after(java.lang.Object,
     * java.lang.Object, java.lang.Object, com.absir.aop.AopProxyHandler,
     * java.lang.reflect.Method, java.lang.Object[], java.lang.Throwable)
     */
    @Override
    public Object after(Object proxy, Object returnValue, TransactionManager interceptor, AopProxyHandler proxyHandler, Method method, Object[] args, Throwable e) throws Throwable {
        e = interceptor.close(e);
        if (e != null) {
            throw e;
        }

        return returnValue;
    }
}
