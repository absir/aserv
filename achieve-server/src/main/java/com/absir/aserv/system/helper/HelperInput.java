/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-13 下午3:55:01
 */
package com.absir.aserv.system.helper;

import javax.servlet.http.HttpServletRequest;

import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 * 
 */
public class HelperInput {

	/**
	 * @param input
	 * @return
	 */
	public static boolean isAjax(Input input) {
		return input instanceof InputRequest && (((InputRequest) input).getRequest()).getHeader("X-Requested-With") != null;
	}

	/**
	 * @param request
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		return getRequestUrl(request, 80);
	}

	/**
	 * @param request
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request, int defaultPort) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(request.getProtocol());
		stringBuilder.append("://");
		stringBuilder.append(request.getServerName());
		if (request.getRemotePort() != defaultPort) {
			stringBuilder.append(':');
			stringBuilder.append(request.getRemotePort());
		}

		stringBuilder.append(request.getContextPath());
		stringBuilder.append(request.getServletPath());
		if (!KernelString.isEmpty(request.getQueryString())) {
			stringBuilder.append('?');
			stringBuilder.append(request.getQueryString());
		}

		return stringBuilder.toString();
	}

}
