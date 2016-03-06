/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年12月17日 上午10:00:00
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;

/**
 * @author absir
 *
 */
public class BeanDefineRegister extends BeanDefineWrapper {

	/**
	 * @param beanDefine
	 */
	public BeanDefineRegister(BeanDefine beanDefine) {
		super(beanDefine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.core.BeanDefineWrapper#getBeanObject(com.absir.bean.basis
	 * .BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		return beanDefine.getBeanObject(beanFactory);
	}

}
