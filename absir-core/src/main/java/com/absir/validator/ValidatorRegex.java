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
import com.absir.validator.value.Regex;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author absir
 */
@Bean
public class ValidatorRegex extends PropertyResolverAbstract<ValidatorObject, Regex> {

    /**
     * @param propertyObject
     * @param pattern
     * @return
     */
    public ValidatorObject getPropertyObjectPattern(ValidatorObject propertyObject, final String regex) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        final Pattern pattern = java.util.regex.Pattern.compile(regex);
        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value) {
                if (value instanceof String && !pattern.matcher((CharSequence) value).matches()) {
                    return regex + " Regex";
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                validatorMap.put("pattern", regex);
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
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Regex annotation) {
        return getPropertyObjectPattern(propertyObject, annotation.value());
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
        return getPropertyObjectPattern(propertyObject, annotationValue);
    }
}
