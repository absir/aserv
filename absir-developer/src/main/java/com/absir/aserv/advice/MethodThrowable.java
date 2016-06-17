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

public abstract class MethodThrowable extends MethodAdvice {

    @Override
    public Object before(AdviceInvoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return AopProxyHandler.VOID;

        } catch (Exception e) {
            return advice(proxy, method, args, e);
        }
    }

    public abstract Object advice(Object proxy, Method method, Object[] args, Throwable e) throws Throwable;
}
