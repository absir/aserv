/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午1:52:54
 */
package com.absir.context.core;

import com.absir.bean.inject.value.Bean;
import com.absir.context.core.compare.CompareAbstract;
import com.absir.context.core.compare.CompareObject;
import com.absir.context.core.compare.value.CaField;
import com.absir.context.core.compare.value.CaFilter;
import com.absir.core.kernel.KernelClass;
import com.absir.property.PropertySupply;
import com.absir.property.value.PropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
@Bean
public class ContextMapSupply extends PropertySupply<CompareObject, CompareAbstract> {

    private CompareObject compareObject = new CompareObject();

    @Override
    public Class<? extends Annotation> getIngoreAnnotationClass() {
        return null;
    }

    @Override
    public CompareObject getPropertyObject(CompareObject propertyObject, Field field) {
        if (propertyObject == null) {
            if (field.getAnnotation(CaField.class) != null || (KernelClass.isBasicClass(field.getType()) && field.getAnnotation(CaFilter.class) == null)) {
                propertyObject = compareObject;
            }
        }

        return super.getPropertyObject(propertyObject, field);
    }

    @Override
    public CompareObject getPropertyObjectGetter(CompareObject propertyObject, Method method) {
        if (propertyObject == null) {
            if (method.getAnnotation(CaField.class) != null) {
                propertyObject = compareObject;
            }

        } else {
            if (method.getAnnotation(CaFilter.class) != null) {
                propertyObject = null;
            }
        }

        return super.getPropertyObjectGetter(propertyObject, method);
    }

    @Override
    public CompareObject getPropertyObject(CompareObject propertyObject, PropertyInfo[] propertyInfos) {
        for (PropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.value() == CaField.class) {
                if (propertyObject == null) {
                    propertyObject = compareObject;
                }

            } else if (propertyInfo.value() == CaFilter.class) {
                if (propertyObject != null) {
                    propertyObject = null;
                }
            }
        }

        return super.getPropertyObject(propertyObject, propertyInfos);
    }
}
