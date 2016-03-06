/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-2 下午7:06:39
 */
package com.absir.validator;

import java.util.Map;

/**
 * @author absir
 * 
 */
public interface Validator {

	/**
	 * @param value
	 * @return
	 */
	public String validate(Object value);

	/**
	 * @param validatorMap
	 * @return
	 */
	public String getValidateClass(Map<String, Object> validatorMap);

}
