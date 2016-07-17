/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-19 下午2:43:47
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanDefineMethod;
import com.absir.bean.core.BeanFactoryParameters;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Value;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class InjectMethod extends InjectInvokerObserver {

    Method method;

    Method beanMethod;

    String[] paramNames;

    String[] valueNames;

    String[] defaultValues;

    public InjectMethod(Method method, Method beanMethod, String injectName, InjectType injectType) {
        super(injectType);
        this.method = method;
        this.beanMethod = beanMethod == null ? method : beanMethod;
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        this.paramNames = BeanDefineDiscover.parameterNames(method, parameterAnnotations);
        int length = parameterAnnotations.length;
        if (length > 0 && KernelString.isEmpty(paramNames[0]) && !KernelString.isEmpty(injectName)) {
            paramNames[0] = injectName;
        }

        for (int i = 0; i < length; i++) {
            Value value = KernelArray.getAssignable(parameterAnnotations[i], Value.class);
            if (value != null) {
                if (valueNames == null) {
                    valueNames = new String[paramNames.length];
                    defaultValues = new String[paramNames.length];
                }

                valueNames[i] = KernelString.isEmpty(value.value()) ? paramNames[i] : value.value();
                if (!KernelString.isEmpty(value.defaultValue())) {
                    defaultValues[i] = value.defaultValue();
                }
            }
        }
    }

    public Method getMethod() {
        return method;
    }

    @Override
    protected Object parameter(BeanFactory beanFactory) {
        return parameter(beanFactory, method.getParameterTypes());
    }

    public Object parameter(BeanFactory beanFactory, Class<?>[] parameterTypes) {
        if (paramNames == null) {
            return KernelLang.NULL_OBJECTS;
        }

        if (valueNames == null) {
            return BeanDefineMethod.getParameters(beanFactory, parameterTypes, paramNames, injectType == InjectType.Required ? method : null, injectType == InjectType.ObServed);

        } else {
            boolean required = injectType == InjectType.Required;
            boolean invoke = injectType == InjectType.ObServed;
            int length = paramNames.length;
            Object[] parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                Object parameter;
                String valueName = valueNames[i];
                if (valueName == null) {
                    parameter = beanFactory.getBeanObject(paramNames[i], parameterTypes[i], false);

                } else {
                    String paramName = paramNames[i];
                    Class<?> parameterType = parameterTypes[i];
                    parameter = beanFactory.getBeanConfig().getExpressionObject(valueName, paramName, parameterType);
                    if (parameter == null) {
                        valueName = defaultValues[i];
                        if (valueName != null) {
                            parameter = beanFactory.getBeanConfig().getExpressionDefaultValue(valueName, paramName, parameterType);
                        }
                    }
                }

                if (parameter == null) {
                    if (required) {
                        throw new RuntimeException("Can not inject method " + method + " [" + paramNames[i] + "] class = " + parameterTypes[i]);
                    }

                } else {
                    invoke = true;
                    parameters[i] = parameter;
                }
            }

            return invoke ? parameters : null;
        }
    }

    @Override
    protected void invokeImpl(Object beanObject, Object parameter) {
        BeanDefineMethod.getBeanObject(beanObject, beanMethod, (Object[]) parameter);
    }

    public void invoke(BeanFactory beanFactory, Object beanObject, Object... parameters) {
        invoke(new BeanFactoryParameters(beanFactory, parameters), beanObject);
    }

    @Override
    public InjectObserver getInjectObserverImpl() {
        return paramNames == null ? null : new InjectObserverMethod(this);
    }

}
