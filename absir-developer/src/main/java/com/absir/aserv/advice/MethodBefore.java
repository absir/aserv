/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午4:54:30
 */
package com.absir.aserv.advice;

import com.absir.aop.AopProxyHandler;

import java.lang.reflect.Method;

public abstract class MethodBefore<O> extends MethodAdvice<O> {

    @Override
    public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args, O advice) throws Throwable {
        advice(proxy, method, args, advice);
        return AopProxyHandler.VOID;
    }

    public abstract void advice(Object proxy, Method method, Object[] args, O advice);

}
