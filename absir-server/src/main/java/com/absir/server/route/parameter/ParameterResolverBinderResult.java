/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:13:17
 */
package com.absir.server.route.parameter;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.binder.BinderResult;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Base
@Bean
public class ParameterResolverBinderResult implements ParameterResolver<Boolean> {

    @Override
    public Boolean getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return BinderResult.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
    }

    @Override
    public Object getParameterValue(OnPut onPut, Boolean parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        return onPut.getBinderData().getBinderResult();
    }
}
