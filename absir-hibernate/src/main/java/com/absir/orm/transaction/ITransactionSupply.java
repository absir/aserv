/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-3 下午7:06:33
 */
package com.absir.orm.transaction;

import java.util.Map;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public interface ITransactionSupply {

	/**
	 * @return
	 */
	public TransactionContext getTransactionContext();

	/**
	 * @return
	 */
	public Map<String, TransactionContext> getNameMapTransactionContext();

}
