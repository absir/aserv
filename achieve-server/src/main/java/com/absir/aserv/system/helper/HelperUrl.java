/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.system.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author absir
 *
 */
public class HelperUrl {

    /**
     * CONTEXT_PATH_URL
     */
    public static final String CONTEXT_PATH_URL = HelperUrl.class.getName() + "@CONTEXT_PATH_URL";

    /**
     * @param request
     * @return
     */
    public static String getContextUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * @param request
     * @return
     */
    public static String getContextPathUrl(HttpServletRequest request) {
        Object pathUrl = request.getAttribute(CONTEXT_PATH_URL);
        if (pathUrl == null && !(pathUrl instanceof String)) {
            pathUrl = request.getRequestURI().substring(request.getContextPath().length());
            request.setAttribute(CONTEXT_PATH_URL, pathUrl);
        }

        return (String) pathUrl;
    }
}
