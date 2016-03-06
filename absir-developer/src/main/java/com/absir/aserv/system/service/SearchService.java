/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-23 下午1:32:26
 */
package com.absir.aserv.system.service;

import java.io.Serializable;
import java.util.Set;

import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 * 
 */
@Transaction(readOnly = true)
public interface SearchService {

	/**
	 * @param entityName
	 * @param ids
	 * @return
	 */
	public Set<Object> getSearch(String entityName, Object... ids);

	/**
	 * @param entityName
	 * @param ids
	 * @return
	 */
	public Set<Serializable> getSearchIds(String entityName, Object... ids);
}
