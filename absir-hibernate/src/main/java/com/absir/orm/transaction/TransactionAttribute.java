/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

/**
 * @author absir
 * 
 */
public class TransactionAttribute {

	/** readOnly */
	private boolean readOnly;

	/** rollback */
	private Class<?>[] rollback;

	/** nested */
	private boolean nested;

	/** required */
	private boolean required;

	/** timeout */
	private int timeout;

	/**
	 * 
	 */
	public TransactionAttribute() {
	}

	/**
	 * @param readonly
	 * @param rollback
	 * @param nested
	 * @param required
	 * @param timeout
	 * @return
	 */
	public TransactionAttribute(boolean readonly, Class<?>[] rollback, boolean nested, boolean required, int timeout) {
		this.readOnly = readonly;
		this.rollback = rollback;
		this.nested = nested;
		this.timeout = timeout;
		if (required == false && ((rollback != null && rollback.length > 0) || timeout > 0)) {
			required = true;
		}

		this.required = required;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @return the rollback
	 */
	public Class<?>[] getRollback() {
		return rollback;
	}

	/**
	 * @return the nested
	 */
	public boolean isNested() {
		return nested;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}
}
