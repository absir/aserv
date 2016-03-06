/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-7 下午4:18:15
 */
package com.absir.property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 * 
 */
public class PropertyErrors {

	/** propertyErrors */
	private List<PropertyError> propertyErrors = new ArrayList<PropertyError>();

	/**
	 * @return the propertyErrors
	 */
	public List<PropertyError> getPropertyErrors() {
		return propertyErrors;
	}

	/**
	 * @param propertyError
	 */
	public void addPropertyError(PropertyError propertyError) {
		propertyErrors.add(propertyError);
	}

	/**
	 * @param propertyPath
	 * @param errorMessage
	 * @param errorObject
	 */
	public void rejectValue(String propertyPath, String errorMessage, Object errorObject) {
		int size = propertyErrors.size();
		if (size > 0) {
			if (propertyErrors.get(size - 1).getPropertyPath() == propertyPath) {
				return;
			}
		}

		PropertyError propertyError = new PropertyError();
		propertyError.setPropertyPath(propertyPath);
		propertyError.setErrorMessage(errorMessage);
		propertyError.setErrorObject(errorObject);
		propertyErrors.add(propertyError);
	}

	/**
	 * @return
	 */
	public boolean hashErrors() {
		return !propertyErrors.isEmpty();
	}

	/**
	 * @param propertyPath
	 * @return
	 */
	public boolean contain(String propertyPath) {
		for (PropertyError propertyError : propertyErrors) {
			if (propertyError.getPropertyPath().equals(propertyPath)) {
				return true;
			}
		}

		return false;
	}
}
