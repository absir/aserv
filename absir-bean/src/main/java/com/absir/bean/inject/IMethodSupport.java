/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-20 下午12:48:33
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public interface IMethodSupport<T> {

    public T getInject(BeanScope beanScope, BeanDefine beanDefine, Method method);

    public InjectInvoker getInjectInvoker(T inject, Method method, Method beanMethod, Object beanObject, Map<Method, Set<Object>> methodMapInjects);

}
