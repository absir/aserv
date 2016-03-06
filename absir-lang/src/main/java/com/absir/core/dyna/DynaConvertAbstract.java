/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-15 下午7:26:15
 */
package com.absir.core.dyna;

import java.util.Map;

import com.absir.core.kernel.KernelLang.BreakException;

/**
 * @author absir
 * 
 */
public abstract class DynaConvertAbstract implements DynaConvert {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.dyna.DynaConvert#mapTo(java.util.Map,
	 * java.lang.String, java.lang.Class,
	 * com.absir.core.kernel.KernelLang.BreakException)
	 */
	@Override
	public Object mapTo(Map<?, ?> map, String name, Class<?> toClass, BreakException breakException) throws Exception {
		return null;
	}
}
