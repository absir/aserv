/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-14 下午4:03:33
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanDefineProcessor extends Orderable {

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @return
	 */
	public BeanDefine getBeanDefine(BeanFactory beanFactory, BeanDefine beanDefine);

}
