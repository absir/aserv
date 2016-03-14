/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-17 ������9:42:17
 */
package com.absir.bean.core;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.basis.BeanType;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Constructor;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
public class BeanDefineType extends BeanDefineAbstractor {

    /**
     * beanType
     */
    Class<?> beanType;

    /**
     * constructor
     */
    Constructor<?> constructor;

    /**
     * paramNames
     */
    String[] paramNames;

    /**
     * @param beanType
     */
    public BeanDefineType(Class<?> beanType) {
        this(null, beanType);
    }

    /**
     * @param beanName
     * @param beanType
     */
    public BeanDefineType(String beanName, Class<?> beanType) {
        this.constructor = BeanDefineType.getBeanConstructor(beanType);
        this.beanName = getBeanName(beanName, beanType);
        this.paramNames = BeanDefineDiscover.paramterNames(constructor);
        BeanType type = beanType.getAnnotation(BeanType.class);
        if (type == null || !type.value().isAssignableFrom(beanType)) {
            this.beanType = beanType;

        } else {
            this.beanType = type.value();
        }
    }

    /**
     * @param beanName
     * @param beanType
     * @return
     */
    public static String getBeanName(String beanName, Class<?> beanType) {
        return KernelString.isEmpty(beanName) ? KernelString.unCapitalize(beanType.getSimpleName()) : beanName;
    }

    /**
     * @param beanType
     * @return
     */
    public static <T> Constructor<T> getBeanConstructor(Class<T> beanType) {
        return getBeanConstructor(beanType, -1);
    }

    /**
     * @param beanType
     * @param parameterLength
     * @return
     */
    public static <T> Constructor<T> getBeanConstructor(Class<T> beanType, int parameterLength) {
        Constructor<T>[] constructors = (Constructor<T>[]) beanType.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new RuntimeException("can not find constructor of beanType = " + beanType);
        }

        for (Constructor<T> constructor : constructors) {
            int length = constructor.getParameterTypes().length;
            if ((parameterLength <= 0 ? length == 0 || constructor.getAnnotation(Basis.class) != null : length == parameterLength)) {
                constructor.setAccessible(true);
                return constructor;
            }
        }

        if (parameterLength > 0) {
            throw new RuntimeException("can not find constructor of beanType = " + beanType + " parameter length = "
                    + parameterLength);
        }

        Constructor<T> constructor = constructors[0];
        constructor.setAccessible(true);
        return constructor;
    }

    /**
     * @param beanType
     * @return
     */
    public static <T> T getInstanceBean(BeanFactory beanFactory, Class<T> beanType) {
        return getBeanObject(beanFactory, BeanDefineType.getBeanConstructor(beanType));
    }

    /**
     * @param beanFactory
     * @param constructor
     * @return
     */
    public static <T> T getBeanObject(BeanFactory beanFactory, Constructor<T> constructor) {
        return getBeanObject(beanFactory, constructor, constructor.getParameterTypes(),
                BeanDefineDiscover.paramterNames(constructor));
    }

    /**
     * @param beanFactory
     * @param constructor
     * @param paramNames
     * @return
     */
    public static <T> T getBeanObject(BeanFactory beanFactory, Constructor<T> constructor, String[] paramNames) {
        return getBeanObject(beanFactory, constructor, constructor.getParameterTypes(), paramNames);
    }

    /**
     * @param beanFactory
     * @param constructor
     * @param paramNames
     * @return
     */
    public static <T> T getBeanObject(BeanFactory beanFactory, Constructor<T> constructor, Class<?>[] paramTypes,
                                      String[] paramNames) {
        try {
            if (paramNames == null) {
                return constructor.newInstance();
            }

            int length = paramTypes.length;
            Object[] parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                Object parameter = beanFactory.getBeanObject(paramNames[i], paramTypes[i], true);
                if (parameter == null) {
                    throw new RuntimeException("Can not inject " + constructor.getDeclaringClass() + ".constructor = "
                            + constructor + " parameters [" + i + "]");
                }

                parameters[i] = parameter;
            }

            return constructor.newInstance(parameters);

        } catch (Exception e) {
            throw new RuntimeException("Can not instance from " + constructor, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanType()
     */
    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.core.BeanDefineAbstractor#preloadBeanDefine()
     */
    @Override
    public void preloadBeanDefine() {
        KernelClass.forName(beanType.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory)
     */
    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return getBeanObject(beanFactory, constructor, paramNames);
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
        return constructor;
    }
}
