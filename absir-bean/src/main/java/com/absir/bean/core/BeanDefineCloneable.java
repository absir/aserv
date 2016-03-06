/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午8:18:24
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelLang.CloneTemplate;

/**
 * @author absir
 * 
 */
public class BeanDefineCloneable extends BeanDefineAbstract {

	/** beanObject */
	CloneTemplate<?> beanObject;

	/**
	 * @param beanObject
	 */
	public BeanDefineCloneable(CloneTemplate<?> beanObject) {
		this(null, beanObject);
	}

	/**
	 * @param beanName
	 * @param beanObject
	 */
	public BeanDefineCloneable(String beanName, CloneTemplate<?> beanObject) {
		this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
		this.beanObject = beanObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.value.IBeanDefine#getBeanType()
	 */
	@Override
	public Class<?> getBeanType() {
		return beanObject.getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.value.IBeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return BeanScope.PROTOTYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.value.IBeanDefine#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return beanObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		return beanObject.clone();
	}

}
