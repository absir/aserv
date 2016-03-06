/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-16 下午10:58:00
 */
package com.absir.bean.config;

import java.util.List;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public interface IBeanDefineSupply extends Orderable {

	/**
	 * @param beanFactory
	 * @param beanType
	 * @return
	 */
	public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType);

}
