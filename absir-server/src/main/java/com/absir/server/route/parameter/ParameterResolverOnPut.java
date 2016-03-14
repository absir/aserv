/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-16 下午5:12:04
 */
package com.absir.server.route.parameter;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author absir
 */
@Bean
public class ParameterResolverOnPut implements ParameterResolver<Object> {

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
     * java.lang.String[], java.lang.Class<?>[],
     * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
     */
    @Override
    public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return OnPut.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.parameter.ParameterResolver#getParameterValue(
     * com.absir.server.on.OnPut, java.lang.Object, java.lang.Class,
     * java.lang.String, com.absir.server.route.RouteMethod)
     */
    @Override
    public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        return DynaBinder.to(onPut, parameterType);
    }
}
