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
import com.absir.validator.value.Email;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author absir
 */
@Bean
public class ValidatorEmail extends PropertyResolverAbstract<ValidatorObject, Email> {

    /**
     * PATTERN
     */
    public static final Pattern PATTERN = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.property.PropertyResolverAbstract#getPropertyObjectAnnotation
     * (com.absir.property.PropertyObject, java.lang.annotation.Annotation)
     */
    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Email annotation) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value) {
                if (value != null && value instanceof String && !PATTERN.matcher((String) value).find()) {
                    return "Email";
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                return "email";
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
