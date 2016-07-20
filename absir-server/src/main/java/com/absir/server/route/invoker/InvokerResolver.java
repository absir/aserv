/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:00:07
 */
package com.absir.server.route.invoker;

import com.absir.server.on.OnPut;

import java.lang.reflect.Method;

public interface InvokerResolver<T> {

    public T getInvoker(Method method);

    public T getInvoker(Class<?> beanClass);

    public void resolveBefore(T invoker, OnPut onPut) throws Exception;

    public void resolveAfter(Object returnValue, T invoker, OnPut onPut) throws Exception;
}
