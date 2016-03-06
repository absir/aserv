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
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.value.Attribute;

/**
 * @author absir
 * 
 */
@Bean
public class ParameterResolverAttribute implements ParameterResolver<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
	 * java.lang.String[], java.lang.Class<?>[],
	 * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
	 */
	@Override
	public String getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
		Attribute attribute = KernelArray.getAssignable(annotations[i], Attribute.class);
		return attribute == null ? null : KernelString.isEmpty(attribute.value()) ? parameterNames[i] : attribute.value();
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
		Object value = onPut.getBinderData().bind(onPut.getInput().getAttribute(parameter), beanName, parameterType);
		if (value != null) {
			onPut.getInput().setAttribute(parameter, value);
		}

		return value;
	}

}
