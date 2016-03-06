/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年7月16日 下午3:11:28
 */
package com.absir.validator;

/**
 * @author absir
 *
 */
public abstract class ValidatorValue implements Validator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.validator.Validator#validateValue(java.lang.Object)
	 */
	@Override
	public final String validate(Object value) {
		return value == null ? null : validateValue(value);
	}

	/**
	 * @param value
	 * @return
	 */
	public abstract String validateValue(Object value);
}
