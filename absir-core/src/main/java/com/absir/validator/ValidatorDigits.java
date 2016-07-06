/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.validator;

import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelDyna;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Digits;

import java.util.Map;

@Bean
public class ValidatorDigits extends PropertyResolverAbstract<ValidatorObject, Digits> {

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Digits annotation) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value) {
                if (value != null && value instanceof CharSequence) {
                    CharSequence string = (CharSequence) value;
                    if (string.length() > 0 && KernelDyna.to(value, Long.class) == null) {
                        return "Digits";
                    }
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

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        return getPropertyObjectAnnotation(propertyObject, null);
    }
}
