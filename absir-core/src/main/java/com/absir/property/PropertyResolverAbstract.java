/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午12:46:01
 */
package com.absir.property;

import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAnnotation;
import com.absir.property.value.PropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class PropertyResolverAbstract<O extends PropertyObject, A extends Annotation> implements PropertyResolver<O> {

    protected static final TypeVariable ANNOTATION_VARIABLE = PropertyResolverAbstract.class.getTypeParameters()[1];

    private Class<A> annotationClass;

    public PropertyResolverAbstract() {
        annotationClass = KernelClass.typeClass(getClass(), ANNOTATION_VARIABLE);
    }

    @Override
    public O getPropertyObject(O propertyObject, Field field) {
        A annotation = field.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    @Override
    public O getPropertyObjectGetter(O propertyObject, Method method) {
        A annotation = method.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    @Override
    public O getPropertyObjectSetter(O propertyObject, Method method) {
        A annotation = method.getAnnotation(annotationClass);
        return annotation == null ? propertyObject : getPropertyObjectAnnotation(propertyObject, annotation);
    }

    @Override
    public O getPropertyObject(O propertyObject, PropertyInfo[] propertyInfos) {
        for (PropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.annotationType() == annotationClass) {
                return getPropertyObjectAnnotationValue(propertyObject, propertyInfo.valueInfo());
            }
        }

        return propertyObject;
    }

    @Override
    public O getPropertyObjectParams(O propertyObject, String[] propertyParams) {
        String annotationClassName = propertyParams[0];
        if (annotationClassName.equals(annotationClass.getSimpleName())) {
            return getPropertyObjectAnnotation(propertyObject, UtilAnnotation.newInstance(annotationClass, propertyParams, 1));
        }

        return propertyObject;
    }

    public abstract O getPropertyObjectAnnotation(O propertyObject, A annotation);

    public O getPropertyObjectAnnotationValue(O propertyObject, String annotationValue) {
        if (KernelString.isEmpty(annotationValue)) {
            return propertyObject;
        }

        return getPropertyObjectAnnotation(propertyObject, UtilAnnotation.newInstance(annotationClass, annotationValue.split(","), 0));
    }

}
