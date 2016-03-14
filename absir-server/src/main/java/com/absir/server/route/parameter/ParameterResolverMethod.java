/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-26 下午2:01:42
 */
package com.absir.server.route.parameter;

import com.absir.server.in.InMethod;

import java.util.List;

/**
 * @author absir
 *
 */
public interface ParameterResolverMethod {

    /**
     * @param parameter
     * @param inMethods
     * @return
     */
    public List<InMethod> resolveMethods(Object parameter, List<InMethod> inMethods);
}
