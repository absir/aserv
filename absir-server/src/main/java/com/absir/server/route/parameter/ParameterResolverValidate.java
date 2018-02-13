/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:13:17
 */
package com.absir.server.route.parameter;

import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelArray;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.value.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Bean
public class ParameterResolverValidate implements ParameterResolver<Validate> {

    @Inject
    private ParameterResolverBinder parameterResolverBinder;

    @Override
    public Validate getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return KernelArray.getAssignable(annotations[i], Validate.class);
    }

    @Override
    public Object getParameterValue(OnPut onPut, Validate parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        return parameterResolverBinder.getParameterValue(onPut, parameter.value(), parameter.group(), parameter.type(), parameterType, beanName, routeMethod);
    }
}
