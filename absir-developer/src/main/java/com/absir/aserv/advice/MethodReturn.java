/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月10日 下午4:54:30
 */
package com.absir.aserv.advice;

import java.lang.reflect.Method;

public abstract class MethodReturn extends MethodAdvice {

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args, Throwable e) throws Throwable {
        if (e == null) {
            returnValue = advice(proxy, returnValue, method, args);
        }

        return returnValue;
    }

    public abstract Object advice(Object proxy, Object returnValue, Method method, Object[] args);

}
