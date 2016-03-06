/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-20 下午1:27:18
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
public class BeanDefineMerged extends BeanDefineWrappered {

	/** beanName */
	String beanName;

	/** beanScope */
	BeanScope beanScope;

	/** beanComponent */
	private Object beanComponent;

	/**
	 * @param beanDefine
	 * @param beanName
	 * @param beanScope
	 * @param beanComponent
	 */
	public BeanDefineMerged(BeanDefine beanDefine, String beanName, BeanScope beanScope, Object beanComponent) {
		super(beanDefine);
		this.beanName = beanName;
		this.beanScope = beanScope;
		this.beanComponent = beanComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanName()
	 */
	@Override
	public String getBeanName() {
		return beanName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return beanScope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return beanComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory, com.absir.bean.basis.BeanDefine,
	 * com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
		return BeanDefineAbstractor.getBeanObject(beanFactory, beanDefine instanceof BeanDefineWrapper ? ((BeanDefineWrapper) beanDefine).retrenchBeanDefine() : beanDefine, beanDefineRoot, this);
	}
}
