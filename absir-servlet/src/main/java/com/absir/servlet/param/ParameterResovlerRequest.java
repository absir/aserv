/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-5-9 下午4:11:26
 */
package com.absir.servlet.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.ServletRequest;

import com.absir.bean.inject.value.Bean;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
@Bean
public class ParameterResovlerRequest implements ParameterResolver<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
	 * java.lang.String[], java.lang.Class<?>[],
	 * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
	 */
	@Override
	public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
		return ServletRequest.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
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
	public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception {
		Input input = onPut.getInput();
		return input instanceof InputRequest ? ((InputRequest) input).getRequest() : null;
	}
}
