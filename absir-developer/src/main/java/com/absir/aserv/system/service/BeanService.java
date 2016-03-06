/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-28 下午5:24:59
 */
package com.absir.aserv.system.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.service.impl.BeanServiceBase;
import com.absir.aserv.system.service.impl.BeanServiceImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;
import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
@Inject
public interface BeanService {

	/** ME */
	public static final BeanServiceBase ME = BeanFactoryUtils.get(BeanServiceImpl.class);

	/** TRANSACTION_READ_ONLY */
	public static final TransactionAttribute TRANSACTION_READ_ONLY = new TransactionAttribute(true, null, false, true,
			0);

	/** TRANSACTION_READ_WRITE */
	public static final TransactionAttribute TRANSACTION_READ_WRITE = new TransactionAttribute(false,
			new Class[] { Throwable.class }, false, true, 0);

	/**
	 * @author absir
	 *
	 */
	public static class MERGE {

		/**
		 * @param entity
		 * @param id
		 * @param merge
		 * @return
		 */
		public static Object merge(Object entity, Serializable id, CallbackTemplate<Object> merge) {
			if (entity instanceof IMergeService) {
				return ((IMergeService) entity).merge(merge, id);

			} else {
				SessionFactory sessionFactory = SessionFactoryUtils.getSessionFactory(entity.getClass());
				if (sessionFactory != null) {
					TransactionContext transactionContext = TransactionUtils.getTransactionContext(sessionFactory);
					transactionContext.add(BeanService.TRANSACTION_READ_WRITE);
					Throwable ex = null;
					try {
						Session session = sessionFactory.getCurrentSession();
						session.load(entity, id);
						merge.doWith(entity);
						session.flush();

					} catch (Exception e) {
						ex = e;

					} finally {
						transactionContext.closeCurrent(ex, null);
					}
				}
			}

			return entity;
		}
	}

	/**
	 * @param entityClass
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T get(Class<T> entityClass, Serializable id);

	/**
	 * @param entityName
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public Object get(String entityName, Serializable id);

	/**
	 * @param entityName
	 * @param entityClass
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T get(String entityName, Class<T> entityClass, Serializable id);

	/**
	 * @param entityClass
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T find(Class<T> entityClass, Object id);

	/**
	 * @param entityName
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public Object find(String entityName, Object id);

	/**
	 * @param entityName
	 * @param entityClass
	 * @param id
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T find(String entityName, Class<T> entityClass, Object id);

	/**
	 * @param entityClass
	 * @param conditions
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T find(Class<T> entityClass, Object... conditions);

	/**
	 * @param entityName
	 * @param entityClass
	 * @param conditions
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> T find(String entityName, Class<T> entityClass, Object... conditions);

	/**
	 * @param entity
	 */
	@Transaction
	public void persist(Object entity);

	/**
	 * @param entityName
	 * @param entity
	 */
	@Transaction
	public void persist(String entityName, Object entity);

	/**
	 * @param entity
	 */
	@Transaction
	public void update(Object entity);

	/**
	 * @param entityName
	 * @param entity
	 */
	@Transaction
	public void update(String entityName, Object entity);

	/**
	 * @param entity
	 * @return
	 */
	@Transaction
	public <T> T merge(T entity);

	/**
	 * @param entityName
	 * @param entity
	 * @return
	 */
	@Transaction
	public <T> T merge(String entityName, T entity);

	/**
	 * @param entity
	 */
	@Transaction
	public void delete(Object entity);

	/**
	 * @param entityName
	 * @param entity
	 */
	@Transaction
	public void delete(String entityName, Object entity);

	/**
	 * @param entityClass
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> List<T> findAll(Class<T> entityClass);

	/**
	 * @param entityName
	 * @return
	 */
	@Transaction(readOnly = true)
	public List findAll(String entityName);

	/**
	 * @param entityName
	 * @param entityClass
	 * @return
	 */
	@Transaction(readOnly = true)
	public <T> List<T> findAll(String entityName, Class<T> entityClass);

	/**
	 * @param entityName
	 * @param ids
	 * @return
	 */
	@Transaction(readOnly = true)
	public Set<Object> getSearch(String entityName, Object... ids);

	/**
	 * @param entityName
	 * @param ids
	 * @return
	 */
	@Transaction(readOnly = true)
	public Set<Serializable> getSearchIds(String entityName, Object... ids);

	/**
	 * @param entityName
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param conditions
	 * @return
	 */
	@Transaction(readOnly = true)
	public List list(String entityName, String queue, int firstResult, int maxResults, Object... conditions);

	/**
	 * @param entityName
	 * @param queue
	 * @param jdbcPage
	 * @param conditions
	 * @return
	 */
	@Transaction(readOnly = true)
	public List list(String entityName, String queue, JdbcPage jdbcPage, Object... conditions);

	/**
	 * @param entities
	 */
	@Transaction
	public void persists(Collection<?> entities);

	/**
	 * @param entityName
	 * @param entities
	 */
	@Transaction
	public void persists(String entityName, Collection<?> entities);

	/**
	 * @param entities
	 */
	@Transaction
	public void mergers(Collection<?> entities);

	/**
	 * @param entityName
	 * @param entities
	 */
	@Transaction
	public void mergers(String entityName, Collection<?> entities);

	/**
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	@Transaction(readOnly = true)
	public List selectQuery(String queryString, Object... parameters);

	/**
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	@Transaction(readOnly = true)
	public Object selectQuerySingle(String queryString, Object... parameters);

	/**
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	@Transaction
	public int executeUpdate(String queryString, Object... parameters);

}
