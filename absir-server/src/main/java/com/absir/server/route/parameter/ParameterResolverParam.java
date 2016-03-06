/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-30 下午3:13:17
 */
package com.absir.server.route.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.value.Param;

/**
 * @author absir
 * 
 */
@Bean
public class ParameterResolverParam implements ParameterResolver<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
	 * java.lang.String[], java.lang.Class<?>[],
	 * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
	 */
	@Override
	public String getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
		Param param = KernelArray.getAssignable(annotations[i], Param.class);
		return param == null ? null : KernelString.isEmpty(param.value()) ? parameterNames[i] : param.value();
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
	public Object getParameterValue(OnPut onPut, String parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
		Input input = onPut.getInput();
		Object parameterValue = input.getParamMap().get(parameter);
		if (!(parameterValue == null || parameterType.isArray() || !parameterValue.getClass().isArray())) {
			parameterValue = ((Object[]) parameterValue)[0];
		}

		return onPut.getBinderData().bind(parameterValue, beanName, parameterType);
	}

}
