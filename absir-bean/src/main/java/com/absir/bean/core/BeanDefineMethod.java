/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 上午11:14:11
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Method;

public class BeanDefineMethod extends BeanDefineAbstractor {

    BeanDefine beanDefine;

    Method method;

    String[] paramNames;

    Object bean;

    public BeanDefineMethod(Method method) {
        this(null, method);
    }

    public BeanDefineMethod(BeanDefine beanDefine, Method method) {
        this(null, beanDefine, method);
    }

    public BeanDefineMethod(String beanName, BeanDefine beanDefine, Method method) {
        this.beanName = getBeanName(beanName, method);
        this.beanDefine = beanDefine;
        this.method = method;
        this.paramNames = BeanDefineDiscover.paramterNames(method);
    }

    public static String getBeanName(String beanName, Method method) {
        if (KernelString.isEmpty(beanName)) {
            beanName = method.getName();
            if (beanName.length() > 3 && beanName.startsWith("get")) {
                beanName = KernelString.unCapitalize(beanName.substring(3));
            }
        }

        return beanName;
    }

    public static Object getBeanObject(BeanFactory beanFactory, Method method) {
        return getBeanObject(beanFactory, null, method);
    }

    public static Object getBeanObject(BeanFactory beanFactory, Object factory, Method method) {
        return getBeanObject(beanFactory, factory, method, BeanDefineDiscover.paramterNames(method));
    }

    public static Object getBeanObject(BeanFactory beanFactory, Object factory, Method method, String[] paramNames) {
        return getBeanObject(beanFactory, factory, method, paramNames, false);
    }

    public static Object getBeanObject(BeanFactory beanFactory, Object beanObject, Method method, String[] paramNames,
                                       boolean required) {
        return getBeanObject(beanFactory, beanObject, method, paramNames, required, false);
    }

    public static Object getBeanObject(BeanFactory beanFactory, Object beanObject, Method method, String[] paramNames,
                                       boolean required, boolean invoke) {
        if (paramNames == null) {
            try {
                return method.invoke(beanObject);

            } catch (Exception e) {
                throw new RuntimeException("Can not inject " + beanObject + '.' + method, e);
            }
        }

        return getBeanObject(beanObject, method,
                getParameters(beanFactory, method.getParameterTypes(), paramNames, required ? method : null, invoke));
    }

    public static Object getBeanObject(BeanFactory beanFactory, Object beanObject, Method method, Class<?>[] parameterTypes,
                                       String[] paramNames, boolean required, boolean invoke) {
        return getBeanObject(beanObject, method,
                getParameters(beanFactory, method.getParameterTypes(), paramNames, required ? method : null, invoke));
    }

    public static Object[] getParameters(BeanFactory beanFactory, Class<?>[] parameterTypes, String[] paramNames, Object required,
                                         boolean invoke) {
        int length = paramNames.length;
        if (length == 0) {
            return KernelLang.NULL_OBJECTS;
        }

        Object[] parameters = new Object[length];
        for (int i = 0; i < length; i++) {
            Object parameter = beanFactory.getBeanObject(paramNames[i], parameterTypes[i], false);
            if (parameter == null) {
                if (required != null) {
                    throw new RuntimeException("Can not inject " + required + " parameters [" + paramNames[i] + "] class = "
                            + parameterTypes[i]);
                }

            } else {
                invoke = true;
                parameters[i] = parameter;
            }
        }

        return invoke ? parameters : null;
    }

    public static Object getBeanObject(Object beanObject, Method method, Object[] parameters) {
        try {
            return parameters == null ? null : parameters == KernelLang.NULL_OBJECTS ? method.invoke(beanObject) : method.invoke(
                    beanObject, parameters);

        } catch (Exception e) {
            throw new RuntimeException("Can not inject " + beanObject + '.' + method + '[' + KernelString.implode(parameters, ',')
                    + ']', e);
        }
    }

    public BeanDefine getBeanDefine() {
        return beanDefine;
    }

    public void setBeanDefine(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    @Override
    public Class<?> getBeanType() {
        return method.getReturnType();
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        if (beanDefine != null && bean == null) {
            bean = beanDefine.getBeanObject(beanFactory);
            beanDefine = null;
        }

        return getBeanObject(beanFactory, bean, method, paramNames);
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.PROTOTYPE;
    }

    @Override
    public Object getBeanComponent() {
        return method;
    }

    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(method.getDeclaringClass().getName());
    }

}
