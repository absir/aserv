/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午3:13:17
 */
package com.absir.server.route.parameter;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.binder.BinderUtils;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;
import com.absir.server.value.Binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
@Base
@Bean
public class ParameterResolverBinder implements ParameterResolver<Binder> {

    /**
     * BINDER_MAP_NAME
     */
    private static final String BINDER_MAP_NAME = ParameterResolverBinder.class.getName() + "@BINDER_MAP_NAME";

    /**
     * @param input
     * @return
     */
    public static Map<String, Object> getPropertyMap(Input input) {
        Object properties = input.getAttribute(BINDER_MAP_NAME);
        if (properties == null || !(properties instanceof Map)) {
            Map<String, Object> paramMap = input.getParamMap();
            Map<String, Object> propertyMap = BinderUtils.getDataMap(paramMap);
            if (propertyMap == paramMap) {
                propertyMap = new HashMap<String, Object>(paramMap);
            }

            input.setAttribute(BINDER_MAP_NAME, propertyMap);
            return propertyMap;
        }

        return (Map<String, Object>) properties;
    }

    /**
     * @param name
     * @param toClass
     * @param group
     * @param input
     * @return
     */
    public static <T> T getBinderObject(String name, Class<T> toClass, int group, Input input) {
        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().getPropertyFilter().setGroup(group);
        return binderData.mapBind(getPropertyMap(input), name, toClass);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.route.parameter.ParameterResolver#getParameter(int,
     * java.lang.String[], java.lang.Class<?>[],
     * java.lang.annotation.Annotation[][], java.lang.reflect.Method)
     */
    @Override
    public Binder getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return KernelArray.getAssignable(annotations[i], Binder.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.route.parameter.ParameterResolver#getParameterValue(
     * com.absir.server.on.OnPut, java.lang.Object, java.lang.Class,
     * java.lang.String, com.absir.server.route.RouteMethod)
     */
    @Override
    public final Object getParameterValue(OnPut onPut, Binder parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        return getParameterValue(onPut, parameter.value(), parameter.group(), false, parameterType, beanName, routeMethod);
    }

    /**
     * @param onPut
     * @param name
     * @param group
     * @param validation
     * @param parameterType
     * @param beanName
     * @param routeMethod
     * @return
     */
    public Object getParameterValue(OnPut onPut, String name, int group, boolean validation, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        Map<String, Object> propertyMap = getPropertyMap(onPut.getInput());
        BinderData binderData = onPut.getBinderData();
        BinderResult binderResult = binderData.getBinderResult();
        binderResult.setGroup(group);
        binderResult.setValidation(validation);
        return binderData.bind(KernelString.isEmpty(name) ? propertyMap : propertyMap.get(name), beanName, parameterType);
    }
}
