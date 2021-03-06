/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-2 下午7:06:39
 */
package com.absir.validator;

import com.absir.property.Property;
import com.absir.property.PropertyObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValidatorObject implements PropertyObject<List<Validator>> {

    private List<Validator> validators;

    @Override
    public List<Validator> getPropertyData(String name, Property property) {
        if (validators != null) {
            if (validators.isEmpty()) {
                validators = null;
            }
        }

        return validators;
    }

    public void addValidator(Validator validator) {
        if (validators == null) {
            validators = new ArrayList<Validator>();

        } else {
            removeValidatorClass(validator.getClass());
        }

        validators.add(validator);
    }

    public Validator removeValidatorClass(Class<?> validatorClass) {
        if (validators != null) {
            Iterator<Validator> iterator = validators.iterator();
            while (iterator.hasNext()) {
                Validator validator = iterator.next();
                if (validator.getClass() == validatorClass) {
                    iterator.remove();
                    return validator;
                }
            }
        }

        return null;
    }
}
