/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-14 下午1:52:54
 */
package com.absir.context.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.absir.bean.inject.value.Bean;
import com.absir.context.core.compare.CompareAbstract;
import com.absir.context.core.compare.CompareObject;
import com.absir.context.core.compare.value.CaField;
import com.absir.context.core.compare.value.CaFilter;
import com.absir.core.kernel.KernelClass;
import com.absir.property.PropertySupply;
import com.absir.property.value.PropertyInfo;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
@Bean
public class ContextMapSupply extends PropertySupply<CompareObject, CompareAbstract> {

	/** compareObject */
	private CompareObject compareObject = new CompareObject();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertySupply#getIngoreAnnotationClass()
	 */
	@Override
	public Class<? extends Annotation> getIngoreAnnotationClass() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertySupply#getPropertyObject(com.absir.property
	 * .PropertyObject, java.lang.reflect.Field)
	 */
	@Override
	public CompareObject getPropertyObject(CompareObject propertyObject, Field field) {
		if (propertyObject == null) {
			if (field.getAnnotation(CaField.class) != null || (KernelClass.isBasicClass(field.getType()) && field.getAnnotation(CaFilter.class) == null)) {
				propertyObject = compareObject;
			}
		}

		return super.getPropertyObject(propertyObject, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertySupply#getPropertyObjectGetter(java.lang.Object
	 * , java.lang.reflect.Method)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertySupply#getPropertyObject(java.lang.Object,
	 * com.absir.property.value.PropertyInfo[])
	 */
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
