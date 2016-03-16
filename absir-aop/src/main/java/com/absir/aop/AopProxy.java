/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 下午2:05:23
 */
package com.absir.aop;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface AopProxy {

    public Class<?> getBeanType();

    public Object getBeanObject();

    public List<AopInterceptor> getAopInterceptors();
}
