/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 下午3:23:37
 */
package com.absir.server.route;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteParameterPattern extends RouteParameter {

    private Pattern pattern;

    public RouteParameterPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

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

    @Override
    public String toParameterPath(int parameterLength) {
        return pattern.toString();
    }
}
