/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-20 上午10:54:37
 */
package com.absir.server.route;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanDefineWrappered;
import com.absir.server.on.OnScope;

public class RouteDefine extends BeanDefineWrappered {

    private OnScope onScope;

    public RouteDefine(BeanDefine beanDefine, OnScope onScope) {
        super(beanDefine);
        this.onScope = onScope;
    }

    public OnScope getOnScope() {
        return onScope;
    }
}
