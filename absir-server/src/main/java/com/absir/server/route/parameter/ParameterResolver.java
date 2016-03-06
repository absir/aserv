/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-26 下午2:01:42
 */
package com.absir.server.route.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

/**
 * @author absir
 * 
 */
public interface ParameterResolver<T> {

	/**
	 * @param i
	 * @param parameterNames
	 * @param parameterTypes
	 * @param annotations
	 * @param method
	 * @return
	 */
	public T getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method);

	/**
	 * @param onPut
	 * @param parameter
	 * @param parameterType
	 * @param beanName
	 * @param routeMethod
	 * @return
	 * @throws Exception
	 */
	public Object getParameterValue(OnPut onPut, T parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception;
}
