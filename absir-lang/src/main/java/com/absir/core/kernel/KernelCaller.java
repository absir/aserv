/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-29 下午5:25:55
 */
package com.absir.core.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class KernelCaller {

    protected Object target;

    protected Method method;

    public KernelCaller(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public KernelCaller(Object target, String methodName, Class... parameterTypes) {
        this(target, methodName, false, parameterTypes);
    }

    public KernelCaller(Object target, String methodName, boolean assignable, Class... parameterTypes) {
        this.target = target;
        Class targetClass = (target instanceof Class) ? (Class) target : target.getClass();
        this.method = KernelReflect.assignableMethod(targetClass, methodName, 0, false, assignable, false, parameterTypes);
        if (targetClass == target) {
            method = KernelReflect.memberStatic(method);
        }
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object invoke(Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return method.invoke(target, args);
    }
}
