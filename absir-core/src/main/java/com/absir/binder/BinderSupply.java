/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-7 下午2:06:52
 */
package com.absir.binder;

import java.lang.annotation.Annotation;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.binder.value.BinderIngore;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.Property;
import com.absir.property.PropertyData;
import com.absir.property.PropertySupply;

/**
 * @author absir
 * 
 */
@Base
@Bean
public class BinderSupply extends PropertySupply<BinderObject, Binder> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertySupply#getIngoreAnnotationClass()
	 */
	@Override
	public Class<? extends Annotation> getIngoreAnnotationClass() {
		return BinderIngore.class;
	}

	/**
	 * @param propertyData
	 * @param value
	 * @param toType
	 * @param dynaBinder
	 * @param toValue
	 * @return
	 */
	public Object bindValue(PropertyData propertyData, Object value, Class<?> toType, DynaBinder dynaBinder, Object toValue) {
		if (value != null) {
			Property property = propertyData.getProperty();
			String beanName = property.getBeanName();
			if (toType == null) {
				value = property.getPropertyValue(value, beanName);
			}

			Binder binder = getPropertyObject(propertyData);
			if (binder != null) {
				if (toType == null) {
					value = property.getGenericType() == null ? binder.to(value, beanName, property.getType()) : binder.to(value, beanName, property.getGenericType());

				} else {
					value = binder.to(value, beanName, toType);
				}
			}

			if (dynaBinder != null) {
				if (toType == null) {
					value = property.getGenericType() == null ? dynaBinder.bind(value, beanName, property.getType(), toValue) : dynaBinder.bind(value, beanName, property.getGenericType(), toValue);

				} else {
					value = dynaBinder.bind(value, beanName, toType, toValue);
				}
			}
		}

		return value;
	}
}
