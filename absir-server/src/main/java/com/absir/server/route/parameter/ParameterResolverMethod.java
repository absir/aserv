/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-26 下午2:01:42
 */
package com.absir.server.route.parameter;

import java.util.List;

import com.absir.server.in.InMethod;

/**
 * @author absir
 * 
 */
public interface ParameterResolverMethod {

	/**
	 * @param parameter
	 * @param inMethods
	 * @return
	 */
	public List<InMethod> resolveMethods(Object parameter, List<InMethod> inMethods);
}
