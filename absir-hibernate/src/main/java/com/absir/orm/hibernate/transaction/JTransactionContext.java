/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-3 下午4:22:46
 */
package com.absir.orm.hibernate.transaction;

import com.absir.orm.transaction.TransactionContext;

/**
 * @author absir
 * 
 */
public class JTransactionContext extends TransactionContext<JTransactionSession> {

	/** name */
	private String name;

	/**
	 * @param name
	 */
	public JTransactionContext(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.orm.transaction.TransactionContext#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.orm.transaction.TransactionContext#createTransactionSession()
	 */
	@Override
	public JTransactionSession createTransactionSession() {
		return new JTransactionSession();
	}
}
