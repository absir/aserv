/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-8 下午5:37:06
 */
package com.absir.aserv.system.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;

import com.absir.aserv.system.dao.hibernate.BaseDaoImpl;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public abstract class BeanDao {

	/** sessionFactory */
	private static SessionFactory sessionFactory = SessionFactoryUtils.get().getSessionFactory();

	/**
	 * @return
	 */
	public static Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * @param entityClass
	 * @return
	 */
	public static <T> BaseDao<T, ?> getBaseDao(Class<T> entityClass) {
		return BaseDaoImpl.getBaseDaoImpl(entityClass);
	}

	/**
	 * @param entity
	 * @return
	 */
	public static Serializable getIdentifier(Object entity) {
		return SessionFactoryUtils.getIdentifierValue(null, entity, null, null);
	}

	/**
	 * @param entity
	 * @return
	 */
	public static List<Serializable> getIdentifiers(List<Object> entities) {
		List<Serializable> ids = new ArrayList<Serializable>();
		ClassMetadata classMetadata = null;
		for (Object entity : entities) {
			if (classMetadata == null) {
				classMetadata = SessionFactoryUtils.getClassMetadata(null, entity.getClass(), null);
				if (classMetadata == null) {
					break;
				}
			}

			ids.add(SessionFactoryUtils.getIdentifierValue(classMetadata, entity, null));
		}

		return ids;
	}

	/**
	 * @param session
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> T get(Session session, Class<T> entityClass, Serializable id) {
		return id == null ? null : (T) session.get(entityClass, id);
	}

	/**
	 * @param session
	 * @param entityName
	 * @param id
	 * @return
	 */
	public static Object get(Session session, String entityName, Serializable id) {
		return id == null ? null : session.get(SessionFactoryUtils.getEntityName(entityName), id);
	}

	/**
	 * @param session
	 * @param entityName
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> T get(Session session, String entityName, Class<T> entityClass, Serializable id) {
		if (entityName == null) {
			return get(session, entityClass, id);
		}

		Object entity = get(session, entityName, id);
		if (entity == null || !entityClass.isAssignableFrom(entity.getClass())) {
			return null;

		} else {
			return (T) entity;
		}
	}

	/**
	 * @param session
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> T find(Session session, Class<T> entityClass, Object id) {
		return get(session, entityClass,
				DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(null, entityClass, session.getSessionFactory())));
	}

	/**
	 * @param session
	 * @param entityName
	 * @param id
	 * @return
	 */
	public static Object find(Session session, String entityName, Object id) {
		return get(session, entityName,
				DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(entityName, null, session.getSessionFactory())));
	}

	/**
	 * @param session
	 * @param entityName
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> T find(Session session, String entityName, Class<T> entityClass, Object id) {
		return get(session, entityName, entityClass,
				DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(entityName, entityClass, session.getSessionFactory())));
	}

	/**
	 * @param session
	 * @param entityName
	 * @param entity
	 * @return
	 */
	public static Object getLoadedEntity(Session session, String entityName, Object entity) {
		if (session == null) {
			session = SessionFactoryUtils.getSessionFactory(entityName, entity.getClass()).getCurrentSession();
		}

		SessionImpl sessionImpl = (SessionImpl) session;
		EntityEntry entityEntry = sessionImpl.getPersistenceContext().getEntry(entity);
		if (entityEntry != null) {
			EntityPersister entityPersister = entityEntry.getPersister();
			entity = KernelClass.newInstance(entity.getClass());
			Object[] state = entityEntry.getLoadedState();
			int length = state.length;
			for (int i = 0; i < length; i++) {
				entityPersister.setPropertyValue(entity, i, state[i]);
			}

			return entity;
		}

		return get(session, entityName, entity.getClass(), SessionFactoryUtils.getIdentifierValue(null, entity, session, null));
	}

	/**
	 * @param transactionName
	 * @param transactionAttribute
	 * @return
	 */
	public static TransactionContext<?> open(String transactionName, TransactionAttribute transactionAttribute) {
		SessionFactory sessionFactory = SessionFactoryUtils.get().getNameMapSessionFactory(transactionName);
		if (sessionFactory != null) {
			TransactionContext<?> transactionContext = TransactionUtils.getTransactionContext(sessionFactory);
			if (transactionContext != null) {
				transactionContext.add(transactionAttribute);
				return transactionContext;
			}
		}

		return null;
	}

	/**
	 * @param transactionContext
	 * @param e
	 */
	public static void commit(TransactionContext<?> transactionContext, Throwable e) {
		transactionContext.closeCurrent(e, null);
	}
}
