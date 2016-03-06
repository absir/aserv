/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-19 下午2:43:31
 */
package com.absir.bean.inject;

import java.lang.reflect.Field;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class InjectField extends InjectInvokerObserver {

	/** field */
	Field field;

	/** value */
	String value;

	/**
	 * @param field
	 * @param injectName
	 * @param injectType
	 */
	public InjectField(Field field, String injectName, InjectType injectType) {
		super(injectType);
		this.field = field;
		this.value = KernelString.isEmpty(injectName) ? field.getName() : injectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectInvoker#parameter(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	protected Object parameter(BeanFactory beanFactory) {
		return beanFactory.getBeanObject(value, field.getGenericType(), injectType == InjectType.Required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectInvoker#invokeImpl(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected void invokeImpl(Object beanObject, Object parameter) {
		if (parameter == null) {
			if (injectType == InjectType.Required) {
				throw new RuntimeException("Can not inject " + field.getDeclaringClass() + ".field " + field);

			} else if (injectType != InjectType.ObServed) {
				return;
			}
		}

		try {
			field.set(beanObject, parameter);

		} catch (Exception e) {
			throw new RuntimeException("Can not inject " + beanObject + '.' + field + " : " + parameter, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectInvoker#getInjectObserverImpl()
	 */
	@Override
	public InjectObserver getInjectObserverImpl() {
		return new InjectObserverField(this);
	}
}
