/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-2 下午7:06:39
 */
package com.absir.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.absir.property.Property;
import com.absir.property.PropertyObject;

/**
 * @author absir
 * 
 */
public class ValidatorObject implements PropertyObject<List<Validator>> {

	/** validators */
	private List<Validator> validators;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertyObject#getPropertyData(java.lang.String,
	 * com.absir.property.Property)
	 */
	@Override
	public List<Validator> getPropertyData(String name, Property property) {
		if (validators != null) {
			if (validators.isEmpty()) {
				validators = null;
			}
		}

		return validators;
	}

	/**
	 * @param validator
	 */
	public void addValidator(Validator validator) {
		if (validators == null) {
			validators = new ArrayList<Validator>();

		} else {
			removeValidatorClass(validator.getClass());
		}

		validators.add(validator);
	}

	/**
	 * @param validatorClass
	 * @return
	 */
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
