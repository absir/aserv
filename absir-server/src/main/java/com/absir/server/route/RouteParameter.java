/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 下午3:22:06
 */
package com.absir.server.route;

public class RouteParameter {

    public String[] findParameters(String parameterPath, RouteMatcher routeMatcher) {
        return new String[]{parameterPath};
    }

    public String toParameterPath(int parameterLength) {
        return "*";
    }
}
