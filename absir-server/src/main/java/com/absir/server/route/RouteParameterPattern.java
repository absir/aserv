/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-25 下午3:23:37
 */
package com.absir.server.route;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author absir
 * 
 */
public class RouteParameterPattern extends RouteParameter {

	/** pattern */
	private Pattern pattern;

	/**
	 * @param regex
	 */
	public RouteParameterPattern(String regex) {
		this.pattern = Pattern.compile(regex);
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
		Matcher matcher = pattern.matcher(parameterPathName);
		if (matcher.find()) {
			int count = matcher.groupCount();
			String[] parameters = new String[count - 1];
			for (int i = 1; i < count; i++) {
				parameters[i - 1] = matcher.group(i);
			}

			return parameters;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.route.RouteParameter#toParameterPath(int)
	 */
	@Override
	public String toParameterPath(int parameterLength) {
		return pattern.toString();
	}
}
