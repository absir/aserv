package com.absir.server.handler;

import java.lang.reflect.Method;

/**
 * Created by absir on 2016/10/31.
 */
public interface IHandlerRoute {

    public String getHandlerUri(String rpcName, Method method);

}
