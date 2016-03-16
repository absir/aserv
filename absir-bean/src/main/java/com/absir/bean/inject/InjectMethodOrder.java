/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-18 下午1:39:44
 */
package com.absir.bean.inject;

import com.absir.bean.inject.value.InjectType;

import java.lang.reflect.Method;

public class InjectMethodOrder extends InjectMethod {

    int order;

    public InjectMethodOrder(Method method, Method beanMethod, String injectName, InjectType injectType, int order) {
        super(method, beanMethod, injectName, injectType);
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

}
