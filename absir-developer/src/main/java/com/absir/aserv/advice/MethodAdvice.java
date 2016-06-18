/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午4:56:46
 */
package com.absir.aserv.advice;

import com.absir.aop.AopProxyHandler;

import java.lang.reflect.Method;

public abstract class MethodAdvice<O> implements IMethodAdvice<O> {

    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args, O advice) throws Throwable {
        return AopProxyHandler.VOID;
    }

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e, O advice) throws Throwable {
        return returnValue;
    }
}
