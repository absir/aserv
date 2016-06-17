/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-10 下午4:17:59
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.ParamName;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.inject.value.Value;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Field;

public class InjectValue extends InjectInvoker {

    Field field;

    String name;

    String beanName;

    String defaultValue;

    public InjectValue(Field field, Value value) {
        this.field = field;
        name = KernelString.isEmpty(value.value()) ? field.getName() : value.value();
        ParamName beanName = BeanConfigImpl.getFieldAnnotation(field, ParamName.class);
        if (beanName == null) {
            this.beanName = name;

        } else {
            this.beanName = beanName.value();
        }

        if ("".equals(value.defaultValue())) {
            defaultValue = null;

        } else {
            defaultValue = value.defaultValue();
        }
    }

    @Override
    public void invoke(BeanFactory beanFactory, Object beanObject) {
        Object value = beanFactory.getBeanConfig().getExpressionObject(name, beanName, field.getGenericType());
        if (value == null) {
            if (defaultValue == null) {
                return;

            } else {
                value = beanFactory.getBeanConfig().getExpressionDefaultValue(defaultValue, beanName, field.getGenericType());
                if (value == null) {
                    return;
                }
            }
        }

        KernelReflect.set(beanObject, field, value);
    }
}
