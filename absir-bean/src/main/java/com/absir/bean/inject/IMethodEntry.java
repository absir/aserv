/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-17 上午11:03:47
 */
package com.absir.bean.inject;

import java.lang.reflect.Method;

/**
 * @author absir
 */
public interface IMethodEntry<T> {

    /**
     * @param beanType
     * @param method
     * @param beanDefine
     * @return
     */
    public T getMethod(Class<?> beanType, Method method);

    /**
     * @param define
     * @param beanType
     * @param beanMethod
     * @param method
     * @param beanDefine
     */
    public void setMethodEntry(T define, Class<?> beanType, Method beanMethod, Method method);

}
