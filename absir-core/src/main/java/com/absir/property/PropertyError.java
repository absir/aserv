/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-7 下午4:13:41
 */
package com.absir.property;

/**
 * @author absir
 * 
 */
public class PropertyError {

	/** propertyPath */
	private String propertyPath;

	/** errorMessage */
	private String errorMessage;

	/** errorObject */
	private Object errorObject;

	/**
	 * @return the propertyPath
	 */
	public String getPropertyPath() {
		return propertyPath;
	}

	/**
	 * @param propertyPath
	 *            the propertyPath to set
	 */
	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorObject
	 */
	public Object getErrorObject() {
		return errorObject;
	}

	/**
	 * @param errorObject
	 *            the errorObject to set
	 */
	public void setErrorObject(Object errorObject) {
		this.errorObject = errorObject;
	}
}
