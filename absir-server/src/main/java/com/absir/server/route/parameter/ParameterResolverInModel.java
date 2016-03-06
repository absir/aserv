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
import com.absir.server.in.InModel;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

/**
 * @author absir
 * 
 */
@Bean
public class ParameterResolverInModel implements ParameterResolver<Boolean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
	 * java.lang.String[], java.lang.Class<?>[],
	 * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
	 */
	@Override
	public Boolean getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
		return InModel.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
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
	public Object getParameterValue(OnPut onPut, Boolean parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
		return onPut.getInput().getModel();
	}
}
