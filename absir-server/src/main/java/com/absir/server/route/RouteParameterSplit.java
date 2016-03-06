/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-25 下午3:22:35
 */
package com.absir.server.route;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class RouteParameterSplit extends RouteParameter {

	/** regex */
	private String regex;

	/**
	 * @param regex
	 */
	public RouteParameterSplit(String regex) {
		regex = regex.replace("\\.", "\\\\.");
		regex = regex.replace("\\*", "\\\\*");
		this.regex = regex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.parameter.ParameterPath#findParameters(java.lang
	 * .String)
	 */
	@Override
	public String[] findParameters(String parameterPathName) {
		return parameterPathName.split(regex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.RouteParameter#toParameterPath(int)
	 */
	@Override
	public String toParameterPath(int parameterLength) {
		return parameterLength == 0 ? "" : KernelString.implode(KernelArray.repeat("*", parameterLength), regex);
	}
}
