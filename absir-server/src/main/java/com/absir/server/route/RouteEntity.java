/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 下午12:10:49
 */
package com.absir.server.route;

import com.absir.server.in.Input;

public abstract class RouteEntity {

    Object route;

    RouteEntry routeEntry;

    public Object getRoute() {
        return route;
    }

    public RouteEntry getRouteEntry() {
        return routeEntry;
    }

    public abstract Class<?> getRouteType();

    public abstract Object getRouteBean(Input input);
}
