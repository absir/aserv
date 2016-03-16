/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.core.util;

import com.absir.core.kernel.KernelLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class UtilAbstractHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        if (method.getParameterTypes().length == 0) {
            if (methodName.equals("toString")) {
                return toString();

            } else if (methodName.equals("hashCode")) {
                return hashCode();
            }
        }

        if (method.getParameterTypes().length == 1) {
            if (methodName.equals("equals")) {
                return args.length == 1 ? equals(proxy, args[0]) : equals(proxy, args);
            }
        }

        Object result = invoke(proxy, methodName, args);
        if (result == KernelLang.NULL_OBJECT) {
            result = invokeMethod(proxy, method, args);
        }

        if (result == KernelLang.NULL_OBJECT) {
            new NoSuchMethodException(methodName).printStackTrace();
        }

        return null;
    }

    protected boolean equals(Object proxy, Object arg) {
        return proxy.equals(arg);
    }

    protected Object invoke(Object proxy, String methodName, Object[] args) {
        return KernelLang.NULL_OBJECT;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) {
        return KernelLang.NULL_OBJECT;
    }
}
