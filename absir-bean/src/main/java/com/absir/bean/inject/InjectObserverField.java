/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-17 下午8:20:34
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.InjectType;

/**
 * @author absir
 * 
 */
public class InjectObserverField extends InjectObserver {

	/** injectField */
	InjectField injectField;

	Class<?> beanType;

	/**
	 * @param injectField
	 */
	public InjectObserverField(InjectField injectField) {
		this.injectField = injectField;
		beanType = BeanFactoryImpl.getBeanType(injectField.field.getGenericType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#getComponent()
	 */
	@Override
	public Object getComponent() {
		return injectField.field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#getInjectType()
	 */
	@Override
	public InjectType getInjectType() {
		return injectField.injectType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.inject.InjectObserver#support(com.absir.bean.basis.BeanDefine
	 * )
	 */
	@Override
	protected boolean support(BeanDefine beanDefine) {
		return beanType.isAssignableFrom(beanDefine.getBeanType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#parameter(com.absir.bean.basis.
	 * BeanFactory, com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public Object parameter(BeanFactory beanFactory, BeanDefine beanDefine) {
		return beanFactory.getBeanObject(injectField.value, injectField.field.getGenericType(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#observer(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean observer(Object beanObject, Object parameter) {
		injectField.invokeImpl(beanObject, parameter);
		return false;
	}
}
