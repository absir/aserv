/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-6 下午6:11:57
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.proxy.JiDeveloper;
import com.absir.aserv.system.bean.proxy.JiSub;
import com.absir.aserv.system.bean.proxy.JiTree;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JiEmbed;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BeanServiceBase implements BeanService, ICrudSupply {

    private SessionFactory sessionFactory;

    public BeanServiceBase(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static void addSearchTreeObjects(Object object, Set<Object> jpTrees) {
        if (object != null && object instanceof JbBean) {
            if (jpTrees.add(((JiTree<?>) object)) && object instanceof JiTree) {
                JiTree<?> tree = (JiTree<?>) object;
                if (tree.getChildren() != null) {
                    for (Object t : tree.getChildren()) {
                        addSearchTreeObjects(t, jpTrees);
                    }
                }
            }
        }
    }

    public static void addSearchTreeIds(Object object, Set<Serializable> serializables) {
        if (object != null && object instanceof JbBean) {
            if (serializables.add(((JbBean) object).getId()) && object instanceof JiTree) {
                JiTree<?> tree = (JiTree<?>) object;
                if (tree.getChildren() != null) {
                    for (Object t : tree.getChildren()) {
                        addSearchTreeIds(t, serializables);
                    }
                }
            }
        }
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        return BeanDao.get(getSession(), entityClass, id);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return BeanDao.get(getSession(), entityName, id);
    }

    @Override
    public <T> T get(String entityName, Class<T> entityClass, Serializable id) {
        return BeanDao.get(getSession(), entityName, entityClass, id);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object id) {
        return BeanDao.find(getSession(), entityClass, id);
    }

    @Override
    public Object find(String entityName, Object id) {
        return BeanDao.find(getSession(), entityName, id);
    }

    @Override
    public <T> T find(String entityName, Class<T> entityClass, Object id) {
        return BeanDao.find(getSession(), entityName, entityClass, id);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object... conditions) {
        return (T) QueryDaoUtils.select(getSession(), SessionFactoryUtils.getJpaEntityName(entityClass), conditions);
    }

    @Override
    public <T> T find(String entityName, Class<T> entityClass, Object... conditions) {
        return (T) QueryDaoUtils.select(getSession(), entityName, conditions);
    }

    @Override
    public void persist(Object entity) {
        getSession().persist(entity);
    }

    @Override
    public void persist(String entityName, Object entity) {
        getSession().persist(entityName, entity);
    }

    @Override
    public void update(Object entity) {
        getSession().update(entity);
    }

    @Override
    public void update(String entityName, Object entity) {
        getSession().update(entityName, entity);
    }

    @Override
    public <T> T merge(T entity) {
        Session session = getSession();
        try {
            session.merge(entity);

        } catch (Exception e) {
            session.cancelQuery();
            session.saveOrUpdate(entity);
        }

        return entity;
    }

    @Override
    public <T> T merge(String entityName, T entity) {
        Session session = getSession();
        try {
            session.merge(entity);

        } catch (Exception e) {
            session.cancelQuery();
            session.saveOrUpdate(entityName, entity);
        }

        return entity;
    }

    @Override
    public void delete(Object entity) {
        getSession().delete(entity);
    }

    @Override
    public void delete(String entityName, Object entity) {
        getSession().delete(entityName, entity);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return (List<T>) QueryDaoUtils.selectQuery(getSession(), SessionFactoryUtils.getJpaEntityName(entityClass),
                null, 0, 0);
    }

    @Override
    public List findAll(String entityName) {
        return QueryDaoUtils.selectQuery(getSession(), entityName, null, 0, 0);
    }

    @Override
    public <T> List<T> findAll(String entityName, Class<T> entityClass) {
        return (List<T>) QueryDaoUtils.selectQuery(getSession(), entityName, null, 0, 0);
    }

    @Override
    public Set<Object> getSearch(String entityName, Object... ids) {
        Set<Object> entities = new HashSet<Object>();
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        Class<? extends Serializable> idType = SessionFactoryUtils.getIdentifierType(entityName, entityClass,
                sessionFactory);
        if (JiTree.class.isAssignableFrom(entityClass)) {
            for (Object id : ids) {
                addSearchTreeObjects(get(entityName, KernelDyna.to(id, idType)), entities);
            }

        } else {
            for (Object id : ids) {
                Object entity = get(entityName, KernelDyna.to(id, idType));
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    public Set<Serializable> getSearchIds(String entityName, Object... ids) {
        Set<Serializable> serializables = new HashSet<Serializable>();
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        Class<? extends Serializable> idType = SessionFactoryUtils.getIdentifierType(entityName, entityClass,
                sessionFactory);
        if (JiTree.class.isAssignableFrom(entityClass)) {
            for (Object id : ids) {
                addSearchTreeIds(get(entityName, KernelDyna.to(id, idType)), serializables);
            }

        } else {
            for (Object id : ids) {
                serializables.add(KernelDyna.to(id, idType));
            }
        }

        return serializables;
    }

    @Override
    public List list(String entityName, String queue, int firstResult, int maxResults, Object... conditions) {
        return QueryDaoUtils.selectQuery(getSession(), entityName, conditions, firstResult, maxResults);
    }

    @Override
    public List list(String entityName, String queue, JdbcPage jdbcPage, Object... conditions) {
        return QueryDaoUtils.selectQuery(getSession(), entityName, conditions, jdbcPage);
    }

    @Override
    public void persists(Collection<?> entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.persist(entity);
        }
    }

    @Override
    public void persists(String entityName, Collection<?> entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.persist(entityName, entity);
        }
    }

    @Override
    public void mergers(Collection<?> entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.saveOrUpdate(entity);
        }
    }

    @Override
    public void mergers(String entityName, Collection<?> entities) {
        Session session = getSession();
        for (Object entity : entities) {
            session.saveOrUpdate(entityName, entity);
        }
    }

    @Override
    public List selectQuery(String queryString, Object... parameters) {
        return QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).list();
    }

    @Override
    public Object selectQuerySingle(String queryString, Object... parameters) {
        Iterator iterator = QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).iterate();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public int executeUpdate(String queryString, Object... parameters) {
        return QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).executeUpdate();
    }

    @Override
    public Set<Entry<String, Class<?>>> getEntityNameMapClass() {
        return null;
    }

    @Override
    public String getTransactionName() {
        return "";
    }

    @Override
    public boolean support(Crud crud) {
        return true;
    }

    @Override
    public Class<?> getEntityClass(String entityName) {
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        if (entityClass != null) {
            if (sessionFactory.getClassMetadata(entityClass) == null) {
                return null;
            }
        }

        return entityClass;
    }

    @Override
    public String getIdentifierName(String entityName) {
        return SessionFactoryUtils.getIdentifierName(entityName, null, sessionFactory);
    }

    @Override
    public Class<? extends Serializable> getIdentifierType(String entityName) {
        return SessionFactoryUtils.getIdentifierType(entityName, null, sessionFactory);
    }

    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return SessionFactoryUtils.getIdentifierValue(entityName, entity, null, sessionFactory);
    }

    @Transaction(readOnly = true)
    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        Object entity;
        if (jdbcCondition == null) {
            entity = BeanDao.get(getSession(), entityName, id);

        } else {
            String identifierName = getIdentifierName(entityName);
            if (id instanceof JiEmbed) {
                JiEmbed embed = (JiEmbed) id;
                jdbcCondition.getConditions().add(0, JdbcCondition.ALIAS + "." + identifierName + ".eid = ?");
                jdbcCondition.getConditions().add(1, embed.getEid());
                jdbcCondition.getConditions().add(2, JdbcCondition.ALIAS + "." + identifierName + ".mid = ?");
                jdbcCondition.getConditions().add(3, embed.getMid());

            } else {
                jdbcCondition.getConditions().add(0, JdbcCondition.ALIAS + "." + identifierName + " = ?");
                jdbcCondition.getConditions().add(1, id);
            }

            entity = QueryDaoUtils.selectQuery(getSession(), entityName, jdbcCondition);
        }

        if (entity != null && entity instanceof JiSub) {
            ((JiSub) entity).getSub();
        }

        return LangBundleImpl.ME.getLangProxy(entityName, entity, id);
    }

    @Transaction(readOnly = true)
    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        return LangBundleImpl.ME
                .getLangProxy(entityName,
                        QueryDaoUtils.selectQuery(getSession(), entityName, jdbcCondition,
                                HelperCondition.orderQueue(getEntityClass(entityName), queue), firstResult, maxResults),
                        this);
    }

    @Transaction(readOnly = true)
    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        return LangBundleImpl.ME.getLangProxy(entityName, QueryDaoUtils.selectQuery(getSession(), entityName,
                jdbcCondition, HelperCondition.orderQueue(getEntityClass(entityName), queue), jdbcPage), this);
    }

    @Override
    public Object create(String entityName) {
        return LangBundleImpl.ME.getLangProxy(entityName, KernelClass.newInstance(getEntityClass(entityName)), null);
    }

    @Transaction(rollback = Throwable.class)
    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {
        //&& !(entity instanceof IBase && ((IBase) entity).getId() != null)
        if (create) {
            persist(entityName, entity);

        } else {
            merge(entityName, entity);
        }
    }

    @Transaction(rollback = Throwable.class)
    @Override
    public void deleteEntity(String entityName, Object entity) {
        if (entity instanceof JiDeveloper && ((JiDeveloper) entity).isDeveloper()) {
            return;
        }

        delete(entityName, entity);
    }

    @Override
    public void evict(Object entity) {
        try {
            Session session = getSession();
            if (session != null) {
                session.evict(entity);
            }

        } catch (Throwable e) {
        }
    }

    @Override
    public void flush() {
        Session session = getSession();
        if (session != null) {
            session.flush();
        }
    }
}
