/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 下午5:30:37
 */
package com.absir.property;

import com.absir.core.kernel.KernelList.Orderable;
import com.absir.core.kernel.KernelString;
import com.absir.property.value.Prop;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class PropertyContext implements Orderable {

	/** propertyObject */
	PropertyObject propertyObject;

	/** name */
	String name;

	/** order */
	int order;

	/** include */
	int include;

	/** exclude */
	int exclude;

	/** beanName */
	String beanName;

	/** ignore */
	boolean ignore;

	/** factoryClass */
	Class<? extends PropertyFactory> factoryClass;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return order;
	}

	/**
	 * @param prop
	 */
	public void prop(Prop prop) {
		if (prop != null) {
			if (!KernelString.isEmpty(prop.name())) {
				name = prop.name();
			}

			order = prop.orderProp() ? prop.order() : (order + prop.order());
			include = prop.includeProp() ? prop.include() : (include | prop.include());
			exclude = prop.excludeProp() ? prop.exclude() : (exclude | prop.exclude());
			if (prop.ignore() != 0) {
				ignore = prop.ignore() > 0 ? true : false;
			}

			if (prop.factoryClass() != PropertyFactory.class) {
				if (prop.factoryClass() == PropertyFactory.Void.class) {
					factoryClass = null;

				} else {
					factoryClass = prop.factoryClass();
				}
			}
		}
	}

	/**
	 * @return the propertyObject
	 */
	public Object getPropertyObject() {
		return propertyObject;
	}

	/**
	 * @param name
	 * @param property
	 * @return
	 */
	public Object getPropertyData(String name, Property property) {
		return propertyObject == null ? null : propertyObject.getPropertyData(name, property);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the include
	 */
	public int getInclude() {
		return include;
	}

	/**
	 * @return the exclude
	 */
	public int getExclude() {
		return exclude;
	}

	/**
	 * @return the beanName
	 */
	public String getBeanName() {
		return beanName;
	}
}
