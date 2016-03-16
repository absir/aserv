/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-20 下午3:39:58
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;

import java.lang.reflect.Method;

public interface IMethodDefine<T> {

    public T getDefine(Class<?> beanType, Method method, BeanDefine beanDefine);

    public void setDefine(T define, Class<?> beanType, Method beanMethod, Method method, BeanDefine beanDefine);
}
