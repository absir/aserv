/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-18 下午2:15:51
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public abstract class InjectObserver {

	/**
	 * @return
	 */
	public abstract Object getComponent();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(getComponent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof InjectObserver) {
			return getComponent().equals(((InjectObserver) obj).getComponent());
		}

		return getComponent().equals(obj);
	}

	/**
	 * @return
	 */
	public abstract InjectType getInjectType();

	/**
	 * @param beanDefine
	 * @param register
	 * @return
	 */
	public boolean support(BeanDefine beanDefine, boolean register) {
		return (register || getInjectType() == InjectType.ObServed) && support(beanDefine);
	}

	protected abstract boolean support(BeanDefine beanDefine);

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @return
	 */
	public abstract Object parameter(BeanFactory beanFactory, BeanDefine beanDefine);

	/**
	 * @param beanObject
	 * @param parameter
	 * @return
	 */
	public abstract boolean observer(Object beanObject, Object parameter);

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @param beanObject
	 * @return
	 */
	public boolean observer(BeanFactory beanFactory, BeanDefine beanDefine, Object beanObject) {
		return observer(beanObject, parameter(beanFactory, beanDefine));
	}

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @param beanObject
	 * @param register
	 * @return
	 */
	public boolean changed(BeanFactory beanFactory, BeanDefine beanDefine, Object beanObject, boolean register) {
		if (support(beanDefine, register)) {
			return observer(beanFactory, beanDefine, beanObject);
		}

		return false;
	}
}
