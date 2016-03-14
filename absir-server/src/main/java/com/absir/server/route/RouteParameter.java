/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-25 下午3:22:06
 */
package com.absir.server.route;

/**
 * @author absir
 */
public class RouteParameter {

    /**
     * @param parameterPath
     * @return
     */
    public String[] findParameters(String parameterPath) {
        return new String[]{parameterPath};
    }

    /**
     * @param parameterLength
     * @return
     */
    public String toParameterPath(int parameterLength) {
        return "*";
    }
}
