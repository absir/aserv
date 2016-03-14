/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-15 下午7:36:28
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

import java.lang.reflect.Method;

/**
 * @author absir
 */
public interface IMethodInject<T> {

    /**
     * @return
     */
    public boolean isRequired();

    /**
     * @param beanScope
     * @param beanDefine
     * @param method
     * @return
     */
    public T getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method);

    /**
     * @param inject
     * @param method
     * @param beanObject
     * @param injectMethod
     */
    public void setInjectMethod(T inject, Method method, Object beanObject, InjectMethod injectMethod);
}
