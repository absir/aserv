/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午8:35:22
 */
package com.absir.server.route.returned;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteAction;
import com.absir.server.value.View;

import java.lang.reflect.Method;

/**
 * @author absir
 */
@Base
@Bean
public class ReturnedResolverView implements ReturnedResolver<String> {

    /**
     * ME
     */
    public static final ReturnedResolverView ME = BeanFactoryUtils.get(ReturnedResolverView.class);
    /**
     * ROOT_REGX
     */
    public static final String ROOT_REGX = "^([/\\\\]*)[^/\\\\]*([/\\\\]+)";

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.returned.ReturnedResolver#getReturned(java.lang
     * .reflect.Method)
     */
    @Override
    public String getReturned(Method method) {
        View view = method.getAnnotation(View.class);
        return view == null ? null : view.value();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.returned.ReturnedResolver#getReturned(java.lang
     * .Class)
     */
    @Override
    public String getReturned(Class<?> beanClass) {
        View view = beanClass.getAnnotation(View.class);
        return view == null ? null : view.value();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.returned.ReturnedResolver#resolveReturnedValue
     * (java.lang.Object, java.lang.Object, com.absir.server.on.OnPut)
     */
    @Override
    public void resolveReturnedValue(Object returnValue, String returned, OnPut onPut) throws Exception {
        if (returnValue != null) {
            if (returnValue instanceof String) {
                resolveReturnedView((String) returnValue, onPut);
                return;
            }
        }

        Input input = onPut.getInput();
        RouteAction routeAction = input.getRouteAction();
        if (routeAction == null) {
            if (KernelString.isEmpty(returned)) {
                return;
            }

        } else {
            if (routeAction.getRouteView() == null) {
                String routeView = HelperFileName.normalizeNoEndSeparator(new String(input.getRouteMatcher().getMapping()));
                if (!KernelString.isEmpty(returned)) {
                    // 替换根目录
                    routeView.replaceFirst(ROOT_REGX, "$1" + routeView + "$2");
                }

                routeAction.setRouteView(routeView);
            }

            returned = routeAction.getRouteView();
        }

        resolveReturnedView(returned, onPut);
    }

    /**
     * @param viewValue
     * @param onPut
     * @throws Exception
     */
    public void resolveReturnedView(String view, OnPut onPut) throws Exception {
        onPut.getInput().write(view);
    }
}
