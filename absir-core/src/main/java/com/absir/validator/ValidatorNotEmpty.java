/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.validator;

import com.absir.bean.inject.value.Bean;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.NotEmpty;

import java.util.Map;

@Bean
public class ValidatorNotEmpty extends PropertyResolverAbstract<ValidatorObject, NotEmpty> {

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

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectAnnotation(propertyObject, null);
    }
}
