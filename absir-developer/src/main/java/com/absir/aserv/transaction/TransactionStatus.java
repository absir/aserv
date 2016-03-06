/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-11 下午12:19:53
 */
package com.absir.aserv.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
public class TransactionStatus {

	/** transactionOpened */
	private boolean transactionOpened;

	/** transactionAttribute */
	private TransactionAttribute transactionAttribute;

	/** transactionOpeneds */
	private Set<String> transactionOpeneds;

	/** transactionAttributes */
	private Collection<Entry<TransactionContext, TransactionAttribute>> transactionAttributes;

	/**
	 * @param transactionName
	 * @param transactionAttribute
	 */
	public void open(String transactionName, TransactionAttribute transactionAttribute) {
		if (transactionName == null || "".equals(transactionName)) {
			if (!transactionOpened) {
				this.transactionAttribute = transactionAttribute;
				TransactionUtils.get().getTransactionContext().add(transactionAttribute);
			}

		} else {
			if (transactionOpeneds == null || !transactionOpeneds.contains(transactionName)) {
				TransactionContext transactionContext = TransactionUtils.get().getNameMapTransactionContext(transactionName);
				if (transactionContext != null) {
					transactionContext.add(transactionAttribute);
				}

				if (transactionOpeneds == null) {
					transactionOpeneds = new HashSet<String>();
					transactionAttributes = new ArrayList<Entry<TransactionContext, TransactionAttribute>>();
				}

				transactionOpeneds.add(transactionName);
				transactionAttributes.add(new ObjectEntry<TransactionContext, TransactionAttribute>(transactionContext, transactionAttribute));
			}
		}
	}

	/**
	 * @param e
	 * @return
	 */
	public Throwable closeCurrent(Throwable e) {
		return TransactionUtils.close(transactionAttribute, transactionAttributes, null, e);
	}
}
