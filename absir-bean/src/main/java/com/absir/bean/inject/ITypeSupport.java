/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-24 上午10:04:02
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
public interface ITypeSupport {

	/**
	 * @param beanScope
	 * @param beanDefine
	 * @param type
	 * @return
	 */
	public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Class<?> type);

}
