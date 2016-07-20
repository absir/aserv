/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-16 下午4:33:41
 */
package com.absir.server.route;

import com.absir.server.on.OnPut;

public class RouteException {

    private Class<? extends Throwable>[] exceptions;

    private RouteMethod routeMethod;

    public RouteException(Class<? extends Throwable>[] exceptions, RouteMethod routeMethod) {
        this.exceptions = exceptions;
        this.routeMethod = routeMethod;
    }

    public Class<? extends Throwable>[] getExceptions() {
        return exceptions;
    }

    public RouteMethod getRouteMethod() {
        return routeMethod;
    }

    public boolean invoke(Throwable e, Object routeBean, OnPut onPut) throws Throwable {
        for (Class<? extends Throwable> exception : exceptions) {
            if (exception.isAssignableFrom(e.getClass())) {
                routeMethod.invoke(routeBean, onPut, true);
                return true;
            }
        }

        return false;
    }
}
