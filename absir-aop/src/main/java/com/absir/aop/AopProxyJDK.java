/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AopProxyJDK extends AopProxyHandler implements InvocationHandler {

    public AopProxyJDK(Class<?> beanType, Object beanObject) {
        super(beanType, beanObject);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return intercept(proxy, method, args, null);
    }
}
