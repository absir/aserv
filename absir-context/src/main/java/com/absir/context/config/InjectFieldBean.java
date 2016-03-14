/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-13 下午7:17:35
 */
package com.absir.context.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.ParamName;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectInvoker;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author absir
 */
public class InjectFieldBean extends InjectInvoker {

    /**
     * propertyName
     */
    private String propertyName;

    /**
     * beanDefine
     */
    private BeanDefine beanDefine;

    /**
     * accessor
     */
    private Accessor accessor;

    /**
     * paramName
     */
    private String paramName;

    /**
     * parameterType
     */
    private Type parameterType;

    /**
     * parameterClass
     */
    private Class<?> parameterClass;

    /**
     * @param propertyName
     * @param beanDefine
     */
    public InjectFieldBean(String propertyName, BeanDefine beanDefine) {
        this.propertyName = propertyName;
        this.beanDefine = beanDefine;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.inject.InjectInvoker#invoke(com.absir.bean.basis.BeanFactory
     * , java.lang.Object)
     */
    @Override
    public void invoke(BeanFactory beanFactory, Object beanObject) {
        if (accessor == null) {
            accessor = UtilAccessor.getAccessorObj(beanObject, propertyName, null, false);
            Field field = accessor.getField();
            if (field == null) {
                Method setter = accessor.getSetter();
                if (setter != null) {
                    paramName = BeanDefineDiscover.paramterNames(setter)[0];
                    parameterClass = setter.getParameterTypes()[0];

                } else {
                    parameterClass = Object.class;
                }

            } else {
                ParamName beanName = field.getAnnotation(ParamName.class);
                paramName = beanName == null || KernelString.isEmpty(beanName.value()) ? field.getName() : beanName.value();
                parameterType = field.getGenericType();
            }
        }

        try {
            if (beanDefine instanceof BeanDefineReference) {
                Object value = ((BeanDefineReference) beanDefine).getBeanReference(beanFactory, paramName, parameterType == null ? parameterClass : parameterType);
                if (value != null) {
                    accessor.set(beanObject, value);
                }

            } else {
                Object value = beanDefine.getBeanObject(beanFactory);
                if (parameterType != null) {
                    value = DynaBinder.INSTANCE.bind(value, paramName, parameterType);

                } else if (parameterClass != null) {
                    value = DynaBinder.to(value, paramName, parameterClass);
                }

                accessor.set(beanObject, value);
            }

        } catch (Exception e) {
            if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0) {
                e.printStackTrace();
            }
        }
    }
}
