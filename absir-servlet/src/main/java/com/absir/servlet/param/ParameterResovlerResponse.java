/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-9 下午4:11:26
 */
package com.absir.servlet.param;

import com.absir.bean.inject.value.Bean;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.servlet.InputRequest;

import javax.servlet.ServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Bean
public class ParameterResovlerResponse implements ParameterResolver<Object> {

    @Override
    public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return ServletResponse.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
    }

    @Override
    public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
        Input input = onPut.getInput();
        return input instanceof InputRequest ? ((InputRequest) input).getResponse() : null;
    }
}
