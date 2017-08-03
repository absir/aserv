/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 下午12:51:25
 */
package com.absir.server.route.entity;

import com.absir.server.in.Input;
import com.absir.server.route.RouteEntity;

public class EntitySingleton extends RouteEntity {

    private Object beanObject;

    public EntitySingleton(Object beanObject) {
        this.beanObject = beanObject;
    }

    @Override
    protected Object getRouteBean() {
        return beanObject;
    }

    @Override
    public Class<?> getRouteType() {
        return beanObject.getClass();
    }

    @Override
    public Object getRouteBean(Input input) {
        return beanObject;
    }

}
