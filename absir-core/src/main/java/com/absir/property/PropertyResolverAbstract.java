/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午12:46:01
 */
package com.absir.property;

import com.absir.core.kernel.KernelClass;
import com.absir.property.value.PropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class PropertyResolverAbstract<O extends PropertyObject, A extends Annotation> implements PropertyResolver<O> {

    /**
     * annotationClass
     */
    private Class<A> annotationClass;

    /**
     *
     */
    public PropertyResolverAbstract() {
        annotationClass = KernelClass.argumentClasses(getClass(), true)[1];
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolver#getPropertyObject(com.absir.property
     * .PropertyObject, java.lang.reflect.Field)
     */
    @Override
    public O getPropertyObject(O propertyObject, Field field) {
        A annotation = field.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolver#getPropertyObjectGetter(com.absir
     * .property.PropertyObject, java.lang.reflect.Method)
     */
    @Override
    public O getPropertyObjectGetter(O propertyObject, Method method) {
        A annotation = method.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolver#getPropertyObjectSetter(com.absir
     * .property.PropertyObject, java.lang.reflect.Method)
     */
    @Override
    public O getPropertyObjectSetter(O propertyObject, Method method) {
        A annotation = method.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolver#getPropertyObject(com.absir.property
     * .PropertyObject, com.absir.property.value.PropertyInfo[])
     */
    @Override
    public O getPropertyObject(O propertyObject, PropertyInfo[] propertyInfos) {
        for (PropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.annotationType() == annotationClass) {
                return getPropertyObjectAnnotationValue(propertyObject, propertyInfo.valueInfo());
            }
        }

        return propertyObject;
    }

    /**
     * @param propertyObject
     * @param annotation
     * @return
     */
    public abstract O getPropertyObjectAnnotation(O propertyObject, A annotation);

    /**
     * @param propertyObject
     * @param annotationValue
     * @return
     */
    public abstract O getPropertyObjectAnnotationValue(O propertyObject, String annotationValue);

}
