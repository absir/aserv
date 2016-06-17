/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午12:46:01
 */
package com.absir.property;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelList.Orderable;
import com.absir.property.value.PropertyInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class PropertySupply<O extends PropertyObject<T>, T> {

    public static final TypeVariable O_VARIABLE = PropertySupply.class.getTypeParameters()[0];

    private static int supplySize;

    // 初始化属性参数空间
    static {
        List<PropertySupply> propertySupplies = BeanFactoryUtils.getOrderBeanObjects(PropertySupply.class);
        supplySize = propertySupplies.size();
        for (int i = 0; i < supplySize; i++) {
            propertySupplies.get(i).supplyIndex = i;
        }
    }

    protected Class<? extends Annotation> ingoreAnnotationClass;

    private int supplyIndex;

    private PropertyResolver[] propertyResolvers;

    protected static int getSupplySize() {
        return supplySize;
    }

    @Inject(type = InjectType.Selectable)
    public void setPropertyResolvers(PropertyResolver[] propertyResolvers) {
        List<PropertyResolver> propertyResolveList = new ArrayList<PropertyResolver>();
        Class<?> propertyObjectClass = KernelClass.typeClass(getClass(), O_VARIABLE);
        for (PropertyResolver propertyResolver : propertyResolvers) {
            if (propertyObjectClass == KernelClass.typeClass(propertyResolver.getClass(), PropertyResolver.O_VARIABLE)) {
                propertyResolveList.add(propertyResolver);
            }
        }

        if (!propertyResolveList.isEmpty()) {
            if (!Orderable.class.isAssignableFrom(propertyObjectClass)) {
                KernelList.sortCommonObjects(propertyResolveList);
            }

            this.propertyResolvers = KernelCollection.toArray(propertyResolveList, PropertyResolver.class);
        }

        ingoreAnnotationClass = getIgnoreAnnotationClass();
    }

    public int getSupplyIndex() {
        return supplyIndex;
    }

    public abstract Class<? extends Annotation> getIgnoreAnnotationClass();

    public final Map<String, PropertyData> getPropertyMap(Class<?> beanClass) {
        return PropertyUtils.getPropertyMap(beanClass, this).getNameMapPropertyData();
    }

    public final T getPropertyObject(PropertyData propertyData) {
        return (T) propertyData.getPropertyDatas()[supplyIndex];
    }

    public O getPropertyObject(O propertyObject, Field field) {
        if (propertyResolvers != null) {
            PropertyObject propertyObj = propertyObject;
            for (PropertyResolver propertyResolver : propertyResolvers) {
                propertyObj = propertyResolver.getPropertyObject(propertyObj, field);
            }

            propertyObject = (O) propertyObj;
        }

        return propertyObject;
    }

    public O getPropertyObjectGetter(O propertyObject, Method method) {
        if (propertyResolvers != null) {
            PropertyObject propertyObj = propertyObject;
            if (propertyObj != null && ingoreAnnotationClass != null) {
                if (method.getAnnotation(ingoreAnnotationClass) != null) {
                    propertyObj = null;
                }
            }

            for (PropertyResolver propertyResolver : propertyResolvers) {
                propertyObj = propertyResolver.getPropertyObjectGetter(propertyObj, method);
            }

            propertyObject = (O) propertyObj;
        }

        return propertyObject;
    }

    public O getPropertyObjectSetter(O propertyObject, Method method) {
        if (propertyResolvers != null) {
            PropertyObject propertyObj = propertyObject;
            if (propertyObj != null && ingoreAnnotationClass != null) {
                if (method.getAnnotation(ingoreAnnotationClass) != null) {
                    propertyObj = null;
                }
            }

            for (PropertyResolver propertyResolver : propertyResolvers) {
                propertyObj = propertyResolver.getPropertyObjectSetter(propertyObj, method);
            }

            propertyObject = (O) propertyObj;
        }

        return propertyObject;
    }

    public O getPropertyObject(O propertyObject, PropertyInfo[] propertyInfos) {
        if (propertyResolvers != null) {
            PropertyObject propertyObj = propertyObject;
            if (propertyObj != null && ingoreAnnotationClass != null) {
                for (PropertyInfo propertyInfo : propertyInfos) {
                    if (propertyInfo.getClass() == ingoreAnnotationClass) {
                        propertyObj = null;
                        break;
                    }
                }
            }

            for (PropertyResolver propertyResolver : propertyResolvers) {
                propertyObj = propertyResolver.getPropertyObject(propertyObj, propertyInfos);
            }

            propertyObject = (O) propertyObj;
        }

        return propertyObject;
    }

    public O getPropertyObjectParams(O propertyObject, BeanConfigImpl.ParamsAnnotations annotations) {
        if (propertyResolvers != null) {
            PropertyObject propertyObj = propertyObject;
            if (propertyObj != null && ingoreAnnotationClass != null) {
                if (annotations.findAnnotation(ingoreAnnotationClass)) {
                    propertyObj = null;
                }
            }

            for (PropertyResolver propertyResolver : propertyResolvers) {
                propertyObj = propertyResolver.getPropertyObjectParams(propertyObj, annotations);
            }

            propertyObject = (O) propertyObj;
        }

        return propertyObject;
    }
}
