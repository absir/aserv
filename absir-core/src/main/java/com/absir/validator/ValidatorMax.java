/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.validator;

import java.util.Map;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Max;

/**
 * @author absir
 * 
 */
@Bean
public class ValidatorMax extends PropertyResolverAbstract<ValidatorObject, Max> {

	/**
	 * @param propertyObject
	 * @param max
	 * @return
	 */
	public ValidatorObject getPropertyObjectMax(ValidatorObject propertyObject, final int max) {
		if (propertyObject == null) {
			propertyObject = new ValidatorObject();
		}

		propertyObject.addValidator(new ValidatorValue() {

			@Override
			public String validateValue(Object value) {
				if (DynaBinder.to(value, int.class) > max) {
					return max + " Max";
				}

				return null;
			}

			@Override
			public String getValidateClass(Map<String, Object> validatorMap) {
				if (max < Integer.MAX_VALUE) {
					validatorMap.put("max", max);
				}

				return null;
			}
		});

		return propertyObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Max annotation) {
		return getPropertyObjectMax(propertyObject, annotation.value());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotationValue
	 * (com.absir.property.PropertyObject, java.lang.String)
	 */
	@Override
	public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
		return getPropertyObjectMax(propertyObject, DynaBinder.to(annotationValue, int.class));
	}
}
