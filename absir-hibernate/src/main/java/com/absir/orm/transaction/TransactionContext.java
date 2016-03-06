/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
public abstract class TransactionContext<T extends TransactionSession> {

	/** transactionSessionLocal */
	protected ThreadLocal<T> transactionSessionLocal = new ThreadLocal<T>();

	/**
	 * @return
	 */
	public String getName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	/**
	 * @return
	 */
	public abstract T createTransactionSession();

	/**
	 * @return
	 */
	public T getTransactionSession() {
		return transactionSessionLocal.get();
	}

	/**
	 * @param transactionAttribute
	 */
	public TransactionSession add(TransactionAttribute transactionAttribute) {
		return add(transactionAttribute, false);
	}

	/**
	 * @param transactionAttribute
	 * @param addition
	 * @return
	 */
	public TransactionSession add(TransactionAttribute transactionAttribute, boolean addition) {
		T transactionSession = transactionSessionLocal.get();
		if (transactionSession == null) {
			transactionSession = createTransactionSession();
			transactionSessionLocal.set(transactionSession);
		}

		transactionSession.add(transactionAttribute, addition);
		return transactionSession;
	}

	/**
	 * @param e
	 * @param throwable
	 * @return
	 */
	public Throwable closeCurrent(Throwable e, Throwable throwable) {
		T transactionSession = transactionSessionLocal.get();
		if (transactionSession != null) {
			try {
				if (transactionSession.closeCurrent(e)) {
					transactionSessionLocal.remove();
				}

			} catch (Throwable ex) {
				if (transactionSession.isCloseAll()) {
					transactionSessionLocal.remove();
				}

				KernelLang.ThrowableMutil(ex, throwable == null ? e : throwable);
				throwable = ex;
			}
		}

		return throwable;
	}
}