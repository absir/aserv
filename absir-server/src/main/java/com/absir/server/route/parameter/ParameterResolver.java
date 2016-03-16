/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-26 下午2:01:42
 */
package com.absir.server.route.parameter;

import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface ParameterResolver<T> {

    public T getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method);

    public Object getParameterValue(OnPut onPut, T parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception;
}
