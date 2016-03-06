/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-30 下午2:47:08
 */
package com.absir.server.route.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.server.value.Path;

/**
 * @author absir
 * 
 */
public final class ParameterResolverPath extends ParameterResolverModel {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.parameter.ParameterResolverAttribute#getParameter
	 * (int, java.lang.String[], java.lang.Class<?>[],
	 * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
	 */
	@Override
	public String getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
		Path path = KernelArray.getAssignable(annotations[i], Path.class);
		return path == null || KernelString.isEmpty(path.value()) ? parameterNames[i] : path.value();
	}
}
