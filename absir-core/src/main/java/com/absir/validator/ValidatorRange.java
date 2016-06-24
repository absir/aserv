/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:31:04
 */
package com.absir.validator;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.property.PropertyResolverAbstract;
import com.absir.validator.value.Range;

import java.util.Map;

@Bean
public class ValidatorRange extends PropertyResolverAbstract<ValidatorObject, Range> {

    public ValidatorObject getPropertyObjectLength(ValidatorObject propertyObject, final float min, final float max) {
        if (propertyObject == null) {
            propertyObject = new ValidatorObject();
        }

        propertyObject.addValidator(new ValidatorValue() {

            @Override
            public String validateValue(Object value) {
                float val = DynaBinder.to(value, float.class);
                if (val < min || val > max) {
                    return min + " - " + max + " Range";
                }

                return null;
            }

            @Override
            public String getValidateClass(Map<String, Object> validatorMap) {
                if (min > Integer.MIN_VALUE) {
                    validatorMap.put("min", min);
                }

                if (max < Integer.MAX_VALUE) {
                    validatorMap.put("max", max);
                }

                return null;
            }

        });

        return propertyObject;
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotation(ValidatorObject propertyObject, Range annotation) {
        return getPropertyObjectLength(propertyObject, annotation.min(), annotation.max());
    }

    @Override
    public ValidatorObject getPropertyObjectAnnotationValue(ValidatorObject propertyObject, String annotationValue) {
        String[] parameters = annotationValue.split(",");
        if (parameters.length == 2) {
            return getPropertyObjectLength(propertyObject, DynaBinder.to(parameters[0], float.class), DynaBinder.to(parameters[1], float.class));
        }

        return propertyObject;
    }
}
