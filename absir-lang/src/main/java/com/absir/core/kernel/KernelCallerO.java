/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-30 上午9:35:04
 */
package com.absir.core.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class KernelCallerO extends KernelCaller {

    protected Object[] parameterObjects;

    public KernelCallerO(Object target, Method method) {
        super(target, method);
    }

    public KernelCallerO(Object target, String methodName, Class[] parameterTypes) {
        super(target, methodName, parameterTypes);
    }

    public KernelCallerO(Object target, String methodName, boolean assignable, Class[] parameterTypes) {
        super(target, methodName, assignable, parameterTypes);
    }

    public KernelCallerO(Object target, String methodName, boolean assignable, Class[] parameterTypes, Object... args) {
        super(target, methodName, assignable, parameterTypes);
        setParameterObjects(args);
    }

    public Object[] getParameterObjects() {
        return parameterObjects;
    }

    public void setParameterObjects(Object... args) {
        if (parameterObjects == null) {
            parameterObjects = new Object[method == null ? 0 : method.getParameterTypes().length];
        }

        int length = parameterObjects.length;
        int start = length - args.length;
        if (start < 0) {
            start = 0;
        }

        for (length--; length > start; length--) {
            parameterObjects[length] = args[length - start];
        }
    }

    @Override
    public Object invoke(Object... objects) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (parameterObjects == null) {
            parameterObjects = new Object[method == null ? 0 : method.getParameterTypes().length];
        }

        int length = parameterObjects.length;
        int end = objects.length;
        if (end > length) {
            end = length;
        }

        for (length = 0; length < end; length++) {
            if (objects[length] != KernelLang.NULL_OBJECT) {
                parameterObjects[length] = objects[length];
            }
        }

        return super.invoke(parameterObjects);
    }
}
