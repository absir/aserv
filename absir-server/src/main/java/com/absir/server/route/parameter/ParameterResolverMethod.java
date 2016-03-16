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

public interface ParameterResolverMethod {

    public List<InMethod> resolveMethods(Object parameter, List<InMethod> inMethods);
}
