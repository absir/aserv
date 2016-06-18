/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午4:54:30
 */
package com.absir.aserv.advice;

import java.lang.reflect.Method;

public abstract class MethodAfter<O> extends MethodAdvice<O> {

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e, O advice) throws Throwable {
        if (e == null) {
            advice(proxy, returnValue, method, args, advice);
        }

        return returnValue;
    }

    public abstract void advice(Object proxy, Object returnValue, Method method, Object[] args, O advice);
}
