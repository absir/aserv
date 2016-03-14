/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-8-27 下午4:06:57
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author absir
 */
public interface IAdapterSupport {

    /**
     * @param beanFactory
     * @param bean
     * @param fields
     * @param methods
     */
    public void adapter(BeanFactory beanFactory, Object beanObject, Collection<Field> fields, Collection<Method> methods);

}
