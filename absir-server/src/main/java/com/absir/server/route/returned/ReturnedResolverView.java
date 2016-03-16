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

@Base
@Bean
public class ReturnedResolverView implements ReturnedResolver<String> {

    public static final ReturnedResolverView ME = BeanFactoryUtils.get(ReturnedResolverView.class);

    public static final String ROOT_REGX = "^([/\\\\]*)[^/\\\\]*([/\\\\]+)";

    @Override
    public String getReturned(Method method) {
        View view = method.getAnnotation(View.class);
        return view == null ? null : view.value();
    }

    @Override
    public String getReturned(Class<?> beanClass) {
        View view = beanClass.getAnnotation(View.class);
        return view == null ? null : view.value();
    }

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

    public void resolveReturnedView(String view, OnPut onPut) throws Exception {
        onPut.getInput().write(view);
    }
}
