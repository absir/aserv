/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 上午11:44:29
 */
package com.absir.bean.core;

import java.lang.ref.SoftReference;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
public class BeanDefineSoftReference extends BeanDefineAbstract {

	/** beanType */
	private Class<?> beanType;

	/** softReference */
	protected SoftReference<Object> softReference;

	/**
	 * @param beanType
	 * @param beanObject
	 */
	public BeanDefineSoftReference(Class<?> beanType, Object beanObject) {
		this(beanType, null, beanObject);
	}

	/**
	 * @param beanType
	 * @param beanName
	 * @param beanObject
	 */
	public BeanDefineSoftReference(Class<?> beanType, String beanName, Object beanObject) {
		this.beanType = beanType;
		this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
		this.softReference = new SoftReference<Object>(beanObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.IBeanDefine#getBeanType()
	 */
	@Override
	public Class<?> getBeanType() {
		return beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		return softReference.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.IBeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return BeanScope.SOFTREFERENCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.value.IBeanDefine#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return null;
	}
}
