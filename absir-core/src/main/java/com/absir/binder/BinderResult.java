/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-7 下午4:26:34
 */
package com.absir.binder;

import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.property.PropertyErrors;

/**
 * @author absir
 * 
 */
public class BinderResult extends PropertyErrors {

	/** propertyFilter */
	private PropertyFilter propertyFilter;

	/** validation */
	private boolean validation;

	/**
	 * @return the group
	 */
	public int getGroup() {
		return getPropertyFilter().getGroup();
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(int group) {
		getPropertyFilter().setGroup(group);
	}

	/**
	 * @return the propertyPath
	 */
	public String getPropertyPath() {
		return getPropertyFilter().getPropertyPath();
	}

	/**
	 * @param propertyPath
	 *            the propertyPath to set
	 */
	public void setPropertyPath(String propertyPath) {
		getPropertyFilter().setPropertyPath(propertyPath);
	}

	/**
	 * @return the propertyFilter
	 */
	public PropertyFilter getPropertyFilter() {
		if (propertyFilter == null) {
			propertyFilter = new PropertyFilter();
		}

		return propertyFilter;
	}

	/**
	 * @param propertyFilter
	 *            the propertyFilter to set
	 */
	public void setPropertyFilter(PropertyFilter propertyFilter) {
		this.propertyFilter = propertyFilter;
	}

	/**
	 * @return the validation
	 */
	public boolean isValidation() {
		return validation;
	}

	/**
	 * @param validation
	 *            the validation to set
	 */
	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	/**
	 * 
	 */
	public void ready() {
		if (propertyFilter == null) {
			propertyFilter = new PropertyFilter();

		} else {
			propertyFilter.setPropertyPath("");
		}
	}

	/**
	 * @return
	 */
	public boolean allowPropertyPath() {
		return propertyFilter.isMatch();
	}
}
