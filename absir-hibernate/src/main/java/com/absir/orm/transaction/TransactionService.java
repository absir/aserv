/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-3 上午10:07:02
 */
package com.absir.orm.transaction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.aop.AopMethodDefineAbstract;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.transaction.value.Transactions;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
@Bean
@Basis
public class TransactionService extends AopMethodDefineAbstract<TransactionInterceptor, TransactionManager, String> {

	/** transactionContext */
	private TransactionContext transactionContext;

	/** nameMapTransactionContext */
	private Map<String, TransactionContext> nameMapTransactionContext;

	/**
	 * 
	 */
	@Inject(type = InjectType.Selectable)
	protected void initTransactionSupplies(ITransactionSupply[] transactionSupplies) {
		for (ITransactionSupply transactionSupply : transactionSupplies) {
			TransactionContext transactionContext = transactionSupply.getTransactionContext();
			if (transactionContext != null) {
				this.transactionContext = transactionContext;
			}

			Map<String, TransactionContext> nameMapTransactionContext = transactionSupply
					.getNameMapTransactionContext();
			if (nameMapTransactionContext != null) {
				for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
					setNameMapTransactionContext(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * @return the transactionContext
	 */
	public TransactionContext getTransactionContext() {
		return transactionContext;
	}

	/**
	 * @return the nameMapTransactionContext
	 */
	public TransactionContext getNameMapTransactionContext(String name) {
		return name == null ? transactionContext
				: nameMapTransactionContext == null ? null : nameMapTransactionContext.get(name);
	}

	/**
	 * @param nameMapTransactionContext
	 *            the nameMapTransactionContext to set
	 */
	private void setNameMapTransactionContext(String name, TransactionContext transactionContext) {
		if (nameMapTransactionContext == null) {
			synchronized (this) {
				if (nameMapTransactionContext == null) {
					nameMapTransactionContext = new HashMap<String, TransactionContext>();
				}
			}
		}

		nameMapTransactionContext.put(name, transactionContext);
	}

	/**
	 * @param transactionAttribute
	 */
	public void addTransactionAttributeDefault(TransactionAttribute transactionAttribute) {
		if (transactionContext != null) {
			transactionContext.add(transactionAttribute, true);
		}

		if (nameMapTransactionContext != null) {
			for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
				entry.getValue().add(transactionAttribute, true);
			}
		}
	}

	/**
	 * @param e
	 * @param throwable
	 * @return
	 */
	public Throwable closeAllCurrent(Throwable e, Throwable throwable) {
		if (transactionContext != null) {
			throwable = transactionContext.closeCurrent(e, throwable);
		}

		if (nameMapTransactionContext != null) {
			for (Entry<String, TransactionContext> entry : nameMapTransactionContext.entrySet()) {
				throwable = entry.getValue().closeCurrent(e, throwable);
			}
		}

		return throwable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aop.AopMethodDefine#getAopInterceptor(com.absir.bean.basis.
	 * BeanDefine, java.lang.Object)
	 */
	@Override
	public TransactionInterceptor getAopInterceptor(BeanDefine beanDefine, Object beanObject) {
		return new TransactionInterceptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefineAbstract#getVariable(com.absir.aop.
	 * AopInterceptorAbstract, com.absir.bean.basis.BeanDefine,
	 * java.lang.Object)
	 */
	@Override
	public String getVariable(TransactionInterceptor aopInterceptor, BeanDefine beanDefine, Object beanObject) {
		return beanObject instanceof ITransactionName ? ((ITransactionName) beanObject).getTransactionName() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#setAopInterceptor(java.lang.Object,
	 * com.absir.aop.AopInterceptor, java.lang.Class, java.lang.reflect.Method,
	 * java.lang.reflect.Method)
	 */
	@Override
	public void setAopInterceptor(TransactionManager interceptor, TransactionInterceptor aopInterceptor,
			Class<?> beanType, Method method, Method beanMethod) {
		aopInterceptor.getMethodMapInterceptor().put(beanMethod, interceptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public TransactionManager getAopInterceptor(String variable, Class<?> beanType) {
		Transactions transactions = beanType.getAnnotation(Transactions.class);
		if (transactions != null) {
			TransactionManager transactionManager = new TransactionManager();
			for (Transaction transaction : transactions.value()) {
				transactionManager.setTransactionAttribute(transaction, variable);
			}

			return transactionManager;
		}

		Transaction transaction = beanType.getAnnotation(Transaction.class);
		if (transaction != null) {
			TransactionManager transactionManager = new TransactionManager();
			transactionManager.setTransactionAttribute(transaction, variable);
			return transactionManager;
		}

		Transactional transactional = beanType.getAnnotation(Transactional.class);
		if (transactional != null) {
			TransactionManager transactionManager = new TransactionManager();
			transactionManager.setTransactionAttribute(transactional, variable);
			return transactionManager;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aop.AopMethodDefine#getAopInterceptor(java.lang.Object,
	 * java.lang.Object, java.lang.Class, java.lang.reflect.Method)
	 */
	@Override
	public TransactionManager getAopInterceptor(TransactionManager interceptor, String variable, Class<?> beanType,
			Method method) {
		Transactions transactions = method.getAnnotation(Transactions.class);
		if (transactions == null) {
			Transaction transaction = method.getAnnotation(Transaction.class);
			if (transaction == null) {
				Transactional transactional = beanType.getAnnotation(Transactional.class);
				if (transactional != null) {
					if (interceptor == null) {
						interceptor = new TransactionManager();
					}

					interceptor.setTransactionAttribute(transactional, variable);
				}

			} else {
				if (interceptor == null) {
					interceptor = new TransactionManager();
				}

				interceptor.setTransactionAttribute(transaction, variable);
			}

		} else {
			if (interceptor == null) {
				interceptor = new TransactionManager();
			}

			for (Transaction transaction : transactions.value()) {
				interceptor.setTransactionAttribute(transaction, variable);
			}
		}

		if (interceptor != null) {
			interceptor.unmodifiable();
			if (interceptor.isEmpty()) {
				interceptor = null;
			}
		}

		return interceptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 0;
	}
}
