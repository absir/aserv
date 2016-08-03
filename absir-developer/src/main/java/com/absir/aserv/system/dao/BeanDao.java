/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-8 下午5:37:06
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.system.dao.hibernate.BaseDaoImpl;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class BeanDao {

    private static SessionFactory sessionFactory = SessionFactoryUtils.get().getSessionFactory();

    public static Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public static <T> BaseDao<T, ?> getBaseDao(Class<T> entityClass) {
        return BaseDaoImpl.getBaseDaoImpl(entityClass);
    }

    public static Serializable getIdentifier(Object entity) {
        return SessionFactoryUtils.getIdentifierValue(null, entity, null, null);
    }

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

    public static <T> T get(Session session, Class<T> entityClass, Serializable id) {
        return id == null ? null : (T) session.get(entityClass, id);
    }

    public static Object get(Session session, String entityName, Serializable id) {
        return id == null ? null : session.get(SessionFactoryUtils.getEntityName(entityName), id);
    }

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

    public static <T> T find(Session session, Class<T> entityClass, Object id) {
        return get(session, entityClass,
                DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(null, entityClass, session.getSessionFactory())));
    }

    public static Object find(Session session, String entityName, Object id) {
        return get(session, entityName,
                DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(entityName, null, session.getSessionFactory())));
    }

    public static <T> T find(Session session, String entityName, Class<T> entityClass, Object id) {
        return get(session, entityName, entityClass,
                DynaBinder.to(id, SessionFactoryUtils.getIdentifierType(entityName, entityClass, session.getSessionFactory())));
    }

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

    public static void commit(TransactionContext<?> transactionContext, Throwable e) {
        transactionContext.closeCurrent(e, null);
    }

    public static Object loadReal(Session session, String entityName, Serializable id, LockMode lockMode) {
        if (lockMode == LockMode.NONE) {
            return session.get(entityName, id);
        }

        try {
            Object entity = session.load(entityName, id, lockMode);
            entity.hashCode();
            return entity;

        } catch (ObjectNotFoundException e) {
        }

        return null;
    }

    public static <T> T loadReal(Session session, Class<T> entityClass, Serializable id, LockMode lockMode) {
        if (lockMode == LockMode.NONE) {
            return session.get(entityClass, id);
        }

        try {
            T entity = session.load(entityClass, id, lockMode);
            entity.hashCode();
            return entity;

        } catch (ObjectNotFoundException e) {
        }

        return null;
    }
}
