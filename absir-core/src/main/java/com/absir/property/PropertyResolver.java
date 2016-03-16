/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午12:46:01
 */
package com.absir.property;

import com.absir.property.value.PropertyInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes"})
public interface PropertyResolver<O extends PropertyObject> {

    public O getPropertyObject(O propertyObject, Field field);

    public O getPropertyObjectGetter(O propertyObject, Method method);

    public O getPropertyObjectSetter(O propertyObject, Method method);

    public O getPropertyObject(O propertyObject, PropertyInfo[] propertyInfos);

}
