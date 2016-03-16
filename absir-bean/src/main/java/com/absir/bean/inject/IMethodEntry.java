/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-17 上午11:03:47
 */
package com.absir.bean.inject;

import java.lang.reflect.Method;

public interface IMethodEntry<T> {

    public T getMethod(Class<?> beanType, Method method);

    public void setMethodEntry(T define, Class<?> beanType, Method beanMethod, Method method);

}
