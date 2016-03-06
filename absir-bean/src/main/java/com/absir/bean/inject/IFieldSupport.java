/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-20 下午12:44:08
 */
package com.absir.bean.inject;

import java.lang.reflect.Field;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
public interface IFieldSupport {

	/**
	 * @param beanScope
	 * @param beanDefine
	 * @param field
	 * @return
	 */
	public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Field field);
}
