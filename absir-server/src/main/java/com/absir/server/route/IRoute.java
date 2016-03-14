/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-24 上午10:03:24
 */
package com.absir.server.route;

import com.absir.server.in.InMethod;
import com.absir.server.value.Mapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author absir
 */
public interface IRoute {

    /**
     * @param name
     * @param mapping
     * @param method
     * @param parameterPathNames
     * @param mappings
     * @param inMethods
     */
    public void routeMapping(String name, Entry<Mapping, List<String>> mapping, Method method, List<String> parameterPathNames, List<String> mappings, List<InMethod> inMethods);

}
