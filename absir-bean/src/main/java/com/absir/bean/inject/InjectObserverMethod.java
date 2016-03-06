/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-17 下午8:20:48
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineMethod;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;

/**
 * @author absir
 * 
 */
public class InjectObserverMethod extends InjectObserver {

	/** injectMethod */
	InjectMethod injectMethod;

	/** parameterTypes */
	Class<?>[] parameterTypes;

	/** beanTypes */
	Class<?>[] beanTypes;

	/**
	 * @param injectMethod
	 */
	public InjectObserverMethod(InjectMethod injectMethod) {
		this.injectMethod = injectMethod;
		parameterTypes = injectMethod.method.getParameterTypes();
		int length = parameterTypes.length;
		beanTypes = new Class<?>[length];
		for (int i = 0; i < length; i++) {
			beanTypes[i] = BeanFactoryImpl.getBeanType(parameterTypes[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#getComponent()
	 */
	@Override
	public Object getComponent() {
		return injectMethod.method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#getInjectType()
	 */
	@Override
	public InjectType getInjectType() {
		return injectMethod.injectType;
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
		return KernelClass.isAssignableFrom(beanDefine.getBeanType(), beanTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#parameter(com.absir.bean.basis.
	 * BeanFactory, com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public Object parameter(BeanFactory beanFactory, BeanDefine beanDefine) {
		return injectMethod.parameter(beanFactory, parameterTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectObserver#observer(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean observer(Object beanObject, Object parameter) {
		return KernelDyna.to(BeanDefineMethod.getBeanObject(beanObject, injectMethod.method, (Object[]) parameter), boolean.class);
	}
}
