/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-24 下午3:57:00
 */
package com.absir.aop;

import com.absir.bean.basis.BeanDefine;
import com.absir.core.kernel.KernelList.Orderable;

import java.lang.reflect.Method;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public interface AopMethodDefine<T extends AopInterceptor, K, V> extends Orderable {

    /**
     * @param beanDefine
     * @param beanObject
     * @return
     */
    public T getAopInterceptor(BeanDefine beanDefine, Object beanObject);

    /**
     * @param aopInterceptor
     * @param beanDefine
     * @param beanObject
     * @return
     */
    public V getVariable(T aopInterceptor, BeanDefine beanDefine, Object beanObject);

    /**
     * @param aopInterceptor
     * @return
     */
    public boolean isEmpty(T aopInterceptor);

    /**
     * @param variable
     * @param beanType
     * @return
     */
    public K getAopInterceptor(V variable, Class<?> beanType);

    /**
     * @param interceptor
     * @param variable
     * @param beanType
     * @param method
     * @return
     */
    public K getAopInterceptor(K interceptor, V variable, Class<?> beanType, Method method);

    /**
     * @param interceptor
     * @param aopInterceptor
     * @param beanType
     * @param method
     * @param beanMethod
     */
    public void setAopInterceptor(K interceptor, T aopInterceptor, Class<?> beanType, Method method, Method beanMethod);
}
