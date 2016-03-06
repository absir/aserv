/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-23 下午9:24:27
 */
package com.absir.server.route;

import java.lang.reflect.Method;
import java.util.List;

import com.absir.core.kernel.KernelLang;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.on.OnPut;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverMethod;
import com.absir.server.route.returned.ReturnedResolver;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RouteMethod {

	/** method */
	private Method method;

	/** parameterTypes */
	Class<?>[] parameterTypes;

	/** parameters */
	Object[] parameters;

	/** parameterResolvers */
	ParameterResolver[] parameterResolvers;

	/** beanNames */
	String[] beanNames;

	/** nullables */
	boolean[] nullables;

	/** 返回值不作为服务返回值 */
	boolean noBody;

	/** returned */
	Object returned;

	/** returnedResolver */
	ReturnedResolver returnedResolver;

	/**
	 * @param beanMethod
	 * @param method
	 */
	protected RouteMethod(Method beanMethod) {
		this.method = beanMethod;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the parameterTypes
	 */
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * @return the parameterResolvers
	 */
	public ParameterResolver[] getParameterResolvers() {
		return parameterResolvers;
	}

	/**
	 * @param inMethods
	 * @return
	 */
	public List<InMethod> resolveMethods(List<InMethod> inMethods) {
		if (parameterResolvers != null) {
			int length = parameterResolvers.length;
			for (int i = 0; i < length; i++) {
				ParameterResolver parameterResolver = parameterResolvers[i];
				if (parameterResolver != null && parameterResolver instanceof ParameterResolverMethod) {
					inMethods = ((ParameterResolverMethod) parameterResolver).resolveMethods(parameters[i], inMethods);
				}
			}
		}

		return inMethods;
	}

	/**
	 * @param routeBean
	 * @param onPut
	 * @throws Exception
	 */
	public void invoke(Object routeBean, OnPut onPut) throws Throwable {
		int length = beanNames == null ? 0 : beanNames.length;
		Object parameterValue;
		Object[] parameterValues = length == 0 ? KernelLang.NULL_OBJECTS : new Object[length];
		for (int i = 0; i < length; i++) {
			parameterValue = parameterResolvers[i].getParameterValue(onPut, parameters[i], parameterTypes[i], beanNames[i], this);
			if (parameterValue == null && !nullables[i]) {
				throw new ServerException(ServerStatus.IN_404);
			}

			parameterValues[i] = parameterValue;
		}

		onPut.setReturnedFixed(false);
		parameterValue = method.invoke(routeBean, parameterValues);
		if (!noBody && method.getReturnType() != void.class) {
			onPut.setReturnValue(parameterValue);
		}

		if (returnedResolver != null && !onPut.isReturnedFixed()) {
			onPut.setReturned(returned);
			onPut.setReturnedResolver(returnedResolver);
		}
	}
}
