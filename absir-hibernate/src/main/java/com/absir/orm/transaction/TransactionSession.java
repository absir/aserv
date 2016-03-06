/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-13 下午4:38:14
 */
package com.absir.orm.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author absir
 * 
 */
public class TransactionSession {

	/** addition */
	private boolean addition;

	/** transactionAttribute */
	private TransactionAttribute transactionAttribute;

	/** transactionAttributes */
	private List<TransactionAttribute> transactionAttributes;

	/** sessionHolder */
	private ISessionHolder sessionHolder;

	/** sessionHolders */
	private Stack<ISessionHolder> sessionHolders;

	/**
	 * @param attribute
	 * @param addition
	 */
	public void add(TransactionAttribute attribute, boolean addition) {
		if (this.addition) {
			if (addition) {
				return;
			}

		} else {
			this.addition = true;
		}

		if (transactionAttribute != null) {
			if (transactionAttributes == null) {
				transactionAttributes = new ArrayList<TransactionAttribute>();
			}

			transactionAttributes.add(transactionAttribute);
		}

		transactionAttribute = attribute;
	}

	/**
	 * @param sessionContext
	 */
	public void open(ISessionContext sessionContext) {
		if (transactionAttribute != null) {
			addition = false;
			if (!(transactionAttributes == null || transactionAttributes.isEmpty())) {
				for (TransactionAttribute transactionAttribute : transactionAttributes) {
					holder(sessionContext.open(sessionHolder, transactionAttribute, this));
				}

				transactionAttributes.clear();
			}

			holder(sessionContext.open(sessionHolder, transactionAttribute, this));
			transactionAttribute = null;
		}
	}

	/**
	 * @param holder
	 */
	private void holder(ISessionHolder holder) {
		if (sessionHolder != null) {
			if (sessionHolders == null) {
				sessionHolders = new Stack<ISessionHolder>();
			}

			sessionHolders.add(sessionHolder);
		}

		sessionHolder = holder;
	}

	/**
	 * @param e
	 * @return
	 */
	public boolean closeCurrent(Throwable e) {
		addition = false;
		if (transactionAttribute == null) {
			if (sessionHolder == null) {
				return true;
			}

			ISessionHolder holder = sessionHolder;
			if (sessionHolders == null) {
				sessionHolder = null;

			} else {
				sessionHolder = sessionHolders.isEmpty() ? null : sessionHolders.pop();
			}

			holder.close(e);

		} else {
			if (transactionAttributes != null) {
				int size = transactionAttributes.size();
				if (size > 0) {
					transactionAttribute = transactionAttributes.remove(--size);
					return false;
				}
			}

			transactionAttribute = null;
		}

		return isCloseAll();
	}

	/**
	 * @return
	 */
	public boolean isCloseAll() {
		return transactionAttribute == null && sessionHolders == null;
	}
}
