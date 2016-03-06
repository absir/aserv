/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年8月27日 下午1:58:10
 */
package com.absir.aserv.system.service;

import java.io.Serializable;

import com.absir.core.kernel.KernelLang.CallbackTemplate;

/**
 * @author absir
 *
 */
public interface IMergeService {

	/**
	 * @param merge
	 * @param id
	 * @return
	 */
	public Object merge(CallbackTemplate<Object> merge, Serializable id);

}
