/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-6 下午6:11:57
 */
package com.absir.aserv.system.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BeanServiceBase implements BeanService, ICrudSupply {

	/** sessionFactory */
	private SessionFactory sessionFactory;

	/**
	 * @param sessionFactory
	 */
	public BeanServiceBase(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return
	 */
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#get(java.lang.Class,
	 * java.io.Serializable)
	 */
	@Override
	public <T> T get(Class<T> entityClass, Serializable id) {
		return BeanDao.get(getSession(), entityClass, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#get(java.lang.String,
	 * java.io.Serializable)
	 */
	@Override
	public Object get(String entityName, Serializable id) {
		return BeanDao.get(getSession(), entityName, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#get(java.lang.String,
	 * java.lang.Class, java.io.Serializable)
	 */
	@Override
	public <T> T get(String entityName, Class<T> entityClass, Serializable id) {
		return BeanDao.get(getSession(), entityName, entityClass, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#find(java.lang.Class,
	 * java.lang.Object)
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object id) {
		return BeanDao.find(getSession(), entityClass, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#find(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object find(String entityName, Object id) {
		return BeanDao.find(getSession(), entityName, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#find(java.lang.String,
	 * java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> T find(String entityName, Class<T> entityClass, Object id) {
		return BeanDao.find(getSession(), entityName, entityClass, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#find(java.lang.Class,
	 * java.lang.Object[])
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object... conditions) {
		return (T) QueryDaoUtils.select(getSession(), SessionFactoryUtils.getJpaEntityName(entityClass), conditions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#find(java.lang.String,
	 * java.lang.Class, java.lang.Object[])
	 */
	@Override
	public <T> T find(String entityName, Class<T> entityClass, Object... conditions) {
		return (T) QueryDaoUtils.select(getSession(), entityName, conditions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#persist(java.lang.Object)
	 */
	@Override
	public void persist(Object entity) {
		getSession().persist(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#persist(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void persist(String entityName, Object entity) {
		getSession().persist(entityName, entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#update(java.lang.Object)
	 */
	@Override
	public void update(Object entity) {
		getSession().update(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#update(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void update(String entityName, Object entity) {
		getSession().update(entityName, entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#merge(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#merge(java.lang.String,
	 * java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#delete(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void delete(String entityName, Object entity) {
		getSession().delete(entityName, entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#findAll(java.lang.Class)
	 */
	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		return (List<T>) QueryDaoUtils.selectQuery(getSession(), SessionFactoryUtils.getJpaEntityName(entityClass),
				null, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#findAll(java.lang.String)
	 */
	@Override
	public List findAll(String entityName) {
		return QueryDaoUtils.selectQuery(getSession(), entityName, null, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#findAll(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> List<T> findAll(String entityName, Class<T> entityClass) {
		return (List<T>) QueryDaoUtils.selectQuery(getSession(), entityName, null, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#getSearch(java.lang.String,
	 * java.lang.Object[])
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#getSearchIds(java.lang.
	 * String , java.lang.Object[])
	 */
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

	/**
	 * @param object
	 * @param jpTrees
	 */
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

	/**
	 * @param object
	 * @param serializables
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#list(java.lang.String,
	 * java.lang.String, int, int, java.lang.Object[])
	 */
	@Override
	public List list(String entityName, String queue, int firstResult, int maxResults, Object... conditions) {
		return QueryDaoUtils.selectQuery(getSession(), entityName, conditions, firstResult, maxResults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#list(java.lang.String,
	 * java.lang.String, com.absir.aserv.jdbc.JdbcPage, java.lang.Object[])
	 */
	@Override
	public List list(String entityName, String queue, JdbcPage jdbcPage, Object... conditions) {
		return QueryDaoUtils.selectQuery(getSession(), entityName, conditions, jdbcPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.BeanService#persists(java.util.
	 * Collection )
	 */
	@Override
	public void persists(Collection<?> entities) {
		Session session = getSession();
		for (Object entity : entities) {
			session.persist(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#persists(java.lang.String,
	 * java.util.Collection)
	 */
	@Override
	public void persists(String entityName, Collection<?> entities) {
		Session session = getSession();
		for (Object entity : entities) {
			session.persist(entityName, entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#mergers(java.util.Collection
	 * )
	 */
	@Override
	public void mergers(Collection<?> entities) {
		Session session = getSession();
		for (Object entity : entities) {
			session.saveOrUpdate(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#mergers(java.lang.String,
	 * java.util.Collection)
	 */
	@Override
	public void mergers(String entityName, Collection<?> entities) {
		Session session = getSession();
		for (Object entity : entities) {
			session.saveOrUpdate(entityName, entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#selectQuery(java.lang.String
	 * , java.lang.Object[])
	 */
	@Override
	public List selectQuery(String queryString, Object... parameters) {
		return QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#selectQuerySingle(java.lang
	 * .String, java.lang.Object[])
	 */
	@Override
	public Object selectQuerySingle(String queryString, Object... parameters) {
		Iterator iterator = QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).iterate();
		return iterator.hasNext() ? iterator.next() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.BeanService#excute(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public int executeUpdate(String queryString, Object... parameters) {
		return QueryDaoUtils.createQueryArray(getSession(), queryString, parameters).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getEntityNameMapClass()
	 */
	@Override
	public Set<Entry<String, Class<?>>> getEntityNameMapClass() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getTransactionName()
	 */
	@Override
	public String getTransactionName() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#support(com.absir.aserv.system.bean
	 * .value.JaCrud.Crud)
	 */
	@Override
	public boolean support(Crud crud) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getEntityClass(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierName(java.lang.String)
	 */
	@Override
	public String getIdentifierName(String entityName) {
		return SessionFactoryUtils.getIdentifierName(entityName, null, sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierType(java.lang.String)
	 */
	@Override
	public Class<? extends Serializable> getIdentifierType(String entityName) {
		return SessionFactoryUtils.getIdentifierType(entityName, null, sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getIdentifier(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object getIdentifier(String entityName, Object entity) {
		return SessionFactoryUtils.getIdentifierValue(entityName, entity, null, sessionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#get(java.lang.String,
	 * java.io.Serializable, com.absir.aserv.jdbc.JdbcCondition)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#list(java.lang.String,
	 * com.absir.aserv.jdbc.JdbcCondition, java.lang.String, int, int)
	 */
	@Transaction(readOnly = true)
	@Override
	public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
		return LangBundleImpl.ME
				.getLangProxy(entityName,
						QueryDaoUtils.selectQuery(getSession(), entityName, jdbcCondition,
								HelperCondition.orderQueue(getEntityClass(entityName), queue), firstResult, maxResults),
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#list(java.lang.String,
	 * com.absir.aserv.jdbc.JdbcCondition, java.lang.String,
	 * com.absir.aserv.jdbc.JdbcPage)
	 */
	@Transaction(readOnly = true)
	@Override
	public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
		return LangBundleImpl.ME.getLangProxy(entityName, QueryDaoUtils.selectQuery(getSession(), entityName,
				jdbcCondition, HelperCondition.orderQueue(getEntityClass(entityName), queue), jdbcPage), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#create(java.lang.String)
	 */
	@Override
	public Object create(String entityName) {
		return LangBundleImpl.ME.getLangProxy(entityName, KernelClass.newInstance(getEntityClass(entityName)), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#mergeEntity(java.lang.String,
	 * java.lang.Object, boolean)
	 */
	@Transaction(rollback = Throwable.class)
	@Override
	public void mergeEntity(String entityName, Object entity, boolean create) {
		if (create && !(entity instanceof IBase || ((IBase) entity).getId() != null)) {
			persist(entityName, entity);

		} else {
			merge(entityName, entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#deleteEntity(java.lang.String,
	 * java.lang.Object)
	 */
	@Transaction(rollback = Throwable.class)
	@Override
	public void deleteEntity(String entityName, Object entity) {
		if (entity instanceof JiDeveloper && ((JiDeveloper) entity).isDeveloper()) {
			return;
		}

		delete(entityName, entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#evict(java.lang.Object)
	 */
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
}
