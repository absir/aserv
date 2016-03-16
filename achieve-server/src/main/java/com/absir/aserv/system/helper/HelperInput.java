/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午3:55:01
 */
package com.absir.aserv.system.helper;

import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.servlet.InputRequest;

import javax.servlet.http.HttpServletRequest;

public class HelperInput {

    public static boolean isAjax(Input input) {
        return input instanceof InputRequest && (((InputRequest) input).getRequest()).getHeader("X-Requested-With") != null;
    }

    public static String getRequestUrl(HttpServletRequest request) {
        return getRequestUrl(request, 80);
    }

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
