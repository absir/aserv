/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-20 下午12:48:33
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * @author absir
 */
public interface IMethodSupport<T> {

    /**
     * @param beanScope
     * @param beanDefine
     * @param method
     * @return
     */
    public T getInject(BeanScope beanScope, BeanDefine beanDefine, Method method);

    /**
     * @param inject
     * @param method
     * @param beanMethod
     * @param beanObject
     * @param methodMapInjects
     * @return
     */
    public InjectInvoker getInjectInvoker(T inject, Method method, Method beanMethod, Object beanObject, Map<Method, Set<Object>> methodMapInjects);

}
