/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-14 下午4:11:43
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanObjectProcessor extends Orderable {

	/**
	 * @param beanFactory
	 * @param beanScope
	 * @param beanDefine
	 * @param beanObject
	 * @param beanProxy
	 */
	public void processBeanObject(BeanFactory beanFactory, BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy);

}
