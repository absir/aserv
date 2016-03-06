/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午5:30:07
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineWrappered;

/**
 * @author absir
 * 
 */
public class InjectBeanDefine extends BeanDefineWrappered {

	/** beanScope */
	BeanScope beanScope;

	/**
	 * @param beanDefine
	 */
	public InjectBeanDefine(BeanDefine beanDefine) {
		this(beanDefine, null);
	}

	/**
	 * @param beanDefine
	 * @param beanScope
	 */
	public InjectBeanDefine(BeanDefine beanDefine, BeanScope beanScope) {
		super(beanDefine);
		this.beanScope = beanScope == null ? BeanScope.SINGLETON : beanScope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.BeanDefineWrapper#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return beanScope;
	}
}
