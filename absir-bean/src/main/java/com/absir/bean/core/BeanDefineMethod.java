/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
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

/**
 * @author absir
 */
public class BeanDefineMethod extends BeanDefineAbstractor {

    /**
     * beanDefine
     */
    BeanDefine beanDefine;

    /**
     * method
     */
    Method method;

    /**
     * paramNames
     */
    String[] paramNames;

    /**
     * bean
     */
    Object bean;

    /**
     * @param method
     */
    public BeanDefineMethod(Method method) {
        this(null, method);
    }

    /**
     * @param beanDefine
     * @param method
     */
    public BeanDefineMethod(BeanDefine beanDefine, Method method) {
        this(null, beanDefine, method);
    }

    /**
     * @param beanName
     * @param beanDefine
     * @param method
     */
    public BeanDefineMethod(String beanName, BeanDefine beanDefine, Method method) {
        this.beanName = getBeanName(beanName, method);
        this.beanDefine = beanDefine;
        this.method = method;
        this.paramNames = BeanDefineDiscover.paramterNames(method);
    }

    /**
     * @param beanName
     * @param method
     * @return
     */
    public static String getBeanName(String beanName, Method method) {
        if (KernelString.isEmpty(beanName)) {
            beanName = method.getName();
            if (beanName.length() > 3 && beanName.startsWith("get")) {
                beanName = KernelString.unCapitalize(beanName.substring(3));
            }
        }

        return beanName;
    }

    /**
     * @param beanFactory
     * @param method
     * @return
     */
    public static Object getBeanObject(BeanFactory beanFactory, Method method) {
        return getBeanObject(beanFactory, null, method);
    }

    /**
     * @param beanFactory
     * @param factory
     * @param method
     * @return
     */
    public static Object getBeanObject(BeanFactory beanFactory, Object factory, Method method) {
        return getBeanObject(beanFactory, factory, method, BeanDefineDiscover.paramterNames(method));
    }

    /**
     * @param beanFactory
     * @param factory
     * @param method
     * @param paramNames
     * @return
     */
    public static Object getBeanObject(BeanFactory beanFactory, Object factory, Method method, String[] paramNames) {
        return getBeanObject(beanFactory, factory, method, paramNames, false);
    }

    /**
     * @param beanFactory
     * @param beanObject
     * @param method
     * @param paramNames
     * @param required
     * @return
     */
    public static Object getBeanObject(BeanFactory beanFactory, Object beanObject, Method method, String[] paramNames,
                                       boolean required) {
        return getBeanObject(beanFactory, beanObject, method, paramNames, required, false);
    }

    /**
     * @param beanFactory
     * @param beanObject
     * @param method
     * @param paramNames
     * @param required
     * @param invoke
     * @return
     */
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

    /**
     * @param beanFactory
     * @param beanObject
     * @param method
     * @param parameterTypes
     * @return
     */
    public static Object getBeanObject(BeanFactory beanFactory, Object beanObject, Method method, Class<?>[] parameterTypes,
                                       String[] paramNames, boolean required, boolean invoke) {
        return getBeanObject(beanObject, method,
                getParameters(beanFactory, method.getParameterTypes(), paramNames, required ? method : null, invoke));
    }

    /**
     * @param beanFactory
     * @param method
     * @param parameterTypes
     * @param paramNames
     * @param required
     * @return
     */
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

    /**
     * @param beanObject
     * @param method
     * @param parameters
     * @return
     */
    public static Object getBeanObject(Object beanObject, Method method, Object[] parameters) {
        try {
            return parameters == null ? null : parameters == KernelLang.NULL_OBJECTS ? method.invoke(beanObject) : method.invoke(
                    beanObject, parameters);

        } catch (Exception e) {
            throw new RuntimeException("Can not inject " + beanObject + '.' + method + '[' + KernelString.implode(parameters, ',')
                    + ']', e);
        }
    }

    /**
     * @return
     */
    public BeanDefine getBeanDefine() {
        return beanDefine;
    }

    /**
     * @param beanDefine the beanDefine to set
     */
    public void setBeanDefine(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanType()
     */
    @Override
    public Class<?> getBeanType() {
        return method.getReturnType();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory)
     */
    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        if (beanDefine != null && bean == null) {
            bean = beanDefine.getBeanObject(beanFactory);
            beanDefine = null;
        }

        return getBeanObject(beanFactory, bean, method, paramNames);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanScope()
     */
    @Override
    public BeanScope getBeanScope() {
        return BeanScope.PROTOTYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanComponent()
     */
    @Override
    public Object getBeanComponent() {
        return method;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.core.BeanDefineAbstractor#preloadBeanDefine()
     */
    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(method.getDeclaringClass().getName());
    }

}
