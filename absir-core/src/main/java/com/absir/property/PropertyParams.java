/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-12 下午4:08:01
 */
package com.absir.property;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class PropertyParams implements PropertyFactory {

	/** PARAMS_CONVERT */
	private static final PropertyConvert PARAMS_CONVERT = new PropertyConvert() {

		@Override
		public Object getValue(Object propertyValue) {
			return propertyValue = KernelString.implode(DynaBinder.to(propertyValue, Object[].class), ',');
		}

		@Override
		public Object getPropertyValue(Object value, String beanName) {
			if (value instanceof String) {
				value = ((String) value).split(",");
			}

			return value;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.property.PropertyFactory#getPropertyConvert(com.absir.property
	 * .Property)
	 */
	@Override
	public PropertyConvert getPropertyConvert(Property property) {
		return PARAMS_CONVERT;
	}
}
