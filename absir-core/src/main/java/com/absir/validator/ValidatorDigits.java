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
import com.absir.core.kernel.KernelDyna;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Digits;

/**
 * @author absir
 * 
 */
@Bean
public class ValidatorDigits extends PropertyResolverAbstract<ValidatorObject, Digits> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Digits annotation) {
		if (propertyObject == null) {
			propertyObject = new ValidatorObject();
		}

		propertyObject.addValidator(new ValidatorValue() {

			@Override
			public String validateValue(Object value) {
				if (KernelDyna.to(value, Integer.class) == null) {
					return "Digits";
				}

				return null;
			}

			@Override
			public String getValidateClass(Map<String, Object> validatorMap) {
				return "digits";
			}

		});

		return propertyObject;
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
		return getPropertyObjectAnnotation(propertyObject, null);
	}
}
