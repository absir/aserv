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

@SuppressWarnings("rawtypes")
public interface AopMethodDefine<T extends AopInterceptor, K, V> extends Orderable {

    public T getAopInterceptor(BeanDefine beanDefine, Object beanObject);

    public V getVariable(T aopInterceptor, BeanDefine beanDefine, Object beanObject);

    public boolean isEmpty(T aopInterceptor);

    public K getAopInterceptor(V variable, Class<?> beanType);

    public K getAopInterceptor(K interceptor, V variable, Class<?> beanType, Method method);

    public void setAopInterceptor(K interceptor, T aopInterceptor, Class<?> beanType, Method method, Method beanMethod);
}
