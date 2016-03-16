/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-24 上午9:53:45
 */
package com.absir.server.route;

import java.lang.annotation.Annotation;
import java.util.List;

public class RouteAction {

    private boolean urlDecode;

    private RouteEntity routeEntity;

    private RouteEntry routeEntry;

    private RouteMethod routeMethod;

    private String routeView;

    public RouteAction(boolean urlDecode, RouteEntity routeEntity, RouteEntry routeEntry, RouteMethod routeMethod, String[] parameterPathNames, List<Integer> parameterPathIndexs,
                       List<Annotation[]> parameterAnnotations) {
        this.urlDecode = urlDecode;
        this.routeEntity = routeEntity;
        this.routeEntry = routeEntry;
        this.routeMethod = routeMethod;
    }

    public boolean isUrlDecode() {
        return urlDecode;
    }

    public RouteEntity getRouteEntity() {
        return routeEntity;
    }

    public RouteEntry getRouteEntry() {
        return routeEntry;
    }

    public RouteMethod getRouteMethod() {
        return routeMethod;
    }

    public String getRouteView() {
        return routeView;
    }

    public void setRouteView(String routeView) {
        this.routeView = routeView;
    }
}
