/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-24 上午9:33:27
 */
package com.absir.server.in;

import com.absir.server.on.OnPut;

import java.util.Iterator;

public interface Interceptor {

    public OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable;
}
