/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:13:17
 */
package com.absir.server.route.parameter;

import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.value.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Bean
public class ParameterResolverParam implements ParameterResolver<String> {

    @Override
    public String getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        Param param = KernelArray.getAssignable(annotations[i], Param.class);
        return param == null ? null : KernelString.isEmpty(param.value()) ? parameterNames[i] : param.value();
    }

    @Override
    public Object getParameterValue(OnPut onPut, String parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        Input input = onPut.getInput();
        Object parameterValue = input.getParamMap().get(parameter);
        if (parameterValue != null && parameterValue.getClass().isArray()) {
            if (parameterType.isArray()) {
                if (input.getMethod() == InMethod.GET) {
                    Object[] parameterValues = (Object[]) parameterValue;
                    if (parameterValues.length == 1) {
                        parameterValue = parameterValues[0];
                    }
                }

            } else {
                parameterValue = ((Object[]) parameterValue)[0];
            }
        }

        return onPut.getBinderData().bind(parameterValue, beanName, parameterType);
    }

}
