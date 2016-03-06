/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-4 上午10:14:34
 */
package com.absir.orm.transaction;

/**
 * @author absir
 * 
 */
public abstract class TransactionHolder implements ISessionHolder {

	/** READONLY_FLAG */
	public static final int READONLY_FLAG = 0x01;

	/** READONLY_EQ_FLAG */
	public static final int READONLY_EQ_FLAG = READONLY_FLAG << 1;

	/** NESTED_FLAG */
	public static final int NESTED_FLAG = READONLY_EQ_FLAG << 1;

	/** NESTED_EQ_FLAG */
	public static final int NESTED_EQ_FLAG = NESTED_FLAG << 1;

	/** REQUIRED_FLAG */
	public static final int REQUIRED_FLAG = NESTED_EQ_FLAG << 1;

	/** REQUIRED_EQ_FLAG */
	public static final int REQUIRED_EQ_FLAG = REQUIRED_FLAG << 1;

	/** flag */
	protected int flag;

	/** rollback */
	protected Class<?>[] rollback;

	/** timeout */
	protected long timeout;

	/**
	 * 
	 */
	public TransactionHolder() {
	}

	/**
	 * @param holderBefore
	 * @param transactionAttributeBefore
	 */
	public TransactionHolder(ISessionHolder holderBefore, TransactionAttribute transactionAttribute) {
		if (holderBefore == null || transactionAttribute.isNested()) {
			flag |= NESTED_FLAG;

		} else {
			if (holderBefore.isReadOnly() == transactionAttribute.isReadOnly()) {
				flag |= READONLY_EQ_FLAG;
			}

			// if (holderBefore.isRequired()) {
			// flag |= REQUIRED_FLAG;
			// flag |= REQUIRED_EQ_FLAG;
			//
			// } else
			if (holderBefore.isRequired() == transactionAttribute.isRequired()) {
				flag |= REQUIRED_EQ_FLAG;
			}

			// recard holderBefore timeout
			if (transactionAttribute.getTimeout() > 0) {
				timeout = holderBefore.getTimeout();
				if (timeout < 0 || timeout == transactionAttribute.getTimeout()) {
					timeout = 0;
				}
			}
		}

		if (transactionAttribute.isReadOnly()) {
			flag |= READONLY_FLAG;
		}

		if (transactionAttribute.isRequired()) {
			flag |= REQUIRED_FLAG;
		}

		// self rollback set
		rollback = transactionAttribute.getRollback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.orm.transaction.ISessionHolder#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return (flag & READONLY_FLAG) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.orm.transaction.ISessionHolder#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return (flag & REQUIRED_FLAG) != 0;
	}

	/**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @return the rollback
	 */
	public Class<?>[] getRollback() {
		return rollback;
	}
}
