/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 下午3:22:35
 */
package com.absir.server.route;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import org.apache.commons.lang3.StringUtils;

public class RouteParameterSplit extends RouteParameter {

    private String regex;

    public RouteParameterSplit(String regex) {
        //regex = regex.replace("\\.", "\\\\.");
        //regex = regex.replace("\\*", "\\\\*");
        this.regex = regex;
    }

    @Override
    public String[] findParameters(String parameterPathName, RouteMatcher routeMatcher) {
        if (regex.length() == 1) {
            return KernelString.split(parameterPathName, regex.charAt(0), 0, routeMatcher.getParameterLength());
        }

        return StringUtils.split(parameterPathName, regex);
    }

    @Override
    public String toParameterPath(int parameterLength) {
        return parameterLength == 0 ? "" : KernelString.implode(KernelArray.repeat("*", parameterLength), regex);
    }
}
