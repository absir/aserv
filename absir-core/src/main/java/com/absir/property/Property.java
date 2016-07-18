/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 上午10:34:12
 */
package com.absir.property;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.property.value.Allow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class Property {

    private Accessor accessor;

    private Class<?> type;

    private Type genericType;

    private int allow;

    private int include;

    private int exclude;

    private String beanName;

    private PropertyConvert propertyConvert;

    public Property(Class<?> beanClass, String name, int include, int exclude, String beanName, Class<? extends PropertyFactory> factoryClass) {
        accessor = UtilAccessor.getAccessorProperty(beanClass, name);
        Field field = accessor == null ? null : accessor.getField();
        if (field == null || (!Modifier.isPublic(field.getModifiers()) && PropertyUtils.getFieldAnnotation(beanClass, field, Allow.class) == null)) {
            if (accessor == null) {
                allow = -2;

            } else {
                if (accessor.getGetter() == null) {
                    allow = accessor.getSetter() == null ? -2 : -1;

                } else {
                    allow = accessor.getSetter() == null ? 1 : 0;
                }
            }
        }

        if (allow > -2) {
            if (accessor.getSetter() == null) {
                if (accessor.getGetter() != null) {
                    type = accessor.getGetter().getReturnType();
                }

            } else {
                type = accessor.getSetter().getParameterTypes()[0];
            }

            if (field != null) {
                if (type == null || type.isAssignableFrom(field.getType())) {
                    type = field.getType();
                    genericType = field.getGenericType();
                    if (genericType instanceof TypeVariable) {
                        genericType = KernelClass.type(beanClass, (TypeVariable<?>) genericType);
                        if (genericType != null) {
                            type = KernelClass.rawClass(genericType);
                        }
                    }
                }
            }

            this.include = include;
            this.exclude = exclude;
            this.beanName = beanName;
            this.propertyConvert = factoryClass == null ? null : BeanFactoryUtils.getBeanTypeInstance(factoryClass).getPropertyConvert(this);
        }
    }

    public int getAllow() {
        return allow;
    }

    public boolean isOpened() {
        return allow == 0;
    }

    public boolean isHidden() {
        return allow == 2;
    }

    public boolean isReadable() {
        return allow == 0 || allow == 1;
    }

    public boolean isWriteable() {
        return allow == 0 || allow == -1;
    }

    public Field getField() {
        return accessor.getField();
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public Type getAccessorType() {
        return genericType == null ? type : genericType;
    }

    public int getInclude() {
        return include;
    }

    public int getExclude() {
        return exclude;
    }

    public boolean allow(int group) {
        return KernelLang.PropertyFilter.isAllow(include, exclude, group);
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getValue(Object propertyValue) {
        return propertyValue == null || propertyConvert == null ? propertyValue : propertyConvert.getValue(propertyValue);
    }

    public Object getPropertyValue(Object value, String beanName) {
        return value == null || propertyConvert == null ? value : propertyConvert.getPropertyValue(value, beanName);
    }
}
