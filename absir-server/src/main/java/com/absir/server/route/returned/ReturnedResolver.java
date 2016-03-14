/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:00:07
 */
package com.absir.server.route.returned;

import com.absir.server.on.OnPut;

import java.lang.reflect.Method;

/**
 * @author absir
 */
public interface ReturnedResolver<T> {

    /**
     * @param method
     * @return
     */
    public T getReturned(Method method);

    /**
     * @param beanClass
     * @return
     */
    public T getReturned(Class<?> beanClass);

    /**
     * @param returnValue
     * @param returned
     * @param onPut
     * @throws Exception
     */
    public void resolveReturnedValue(Object returnValue, T returned, OnPut onPut) throws Exception;
}
