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
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.NotEmpty;

/**
 * @author absir
 * 
 */
@Bean
public class ValidatorNotEmpty extends PropertyResolverAbstract<ValidatorObject, NotEmpty> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
	 * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
	 */
	@Override
	public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, NotEmpty annotation) {
		if (propertyObject == null) {
			propertyObject = new ValidatorObject();
		}

		propertyObject.addValidator(new Validator() {

			@Override
			public String validate(Object value) {
				if (value == null || "".equals(value)) {
					return "Required";
				}

				return null;
			}

			@Override
			public String getValidateClass(Map<String, Object> validatorMap) {
				return "required";
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
