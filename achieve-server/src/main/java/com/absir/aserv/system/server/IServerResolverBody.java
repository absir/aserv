/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-11 下午5:27:56
 */
package com.absir.aserv.system.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

/**
 * @author absir
 * 
 */

public interface IServerResolverBody {

	/**
	 * @param i
	 * @param parameterNames
	 * @param parameterTypes
	 * @param annotations
	 * @param method
	 * @return
	 */
	public Integer getBodyParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method);

	/**
	 * @param onPut
	 * @param group
	 * @param parameterType
	 * @param beanName
	 * @param routeMethod
	 * @return
	 * @throws Exception
	 */
	public Object getBodyParameterValue(OnPut onPut, int group, Class<?> parameterType, String beanName, RouteMethod routeMethod) throws Exception;

}
