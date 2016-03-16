/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-16 下午8:19:28
 */
package com.absir.server.in;

import com.absir.server.on.OnPut;

public interface IDispatcher<T> {

    public InMethod getInMethod(T req);

    public String decodeUri(String uri, T req);

    public OnPut onPut(Input input, Object routeBean);

    public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable;

    public boolean returnThrowable(Throwable e, Object routeBean, OnPut onPut) throws Throwable;
}
