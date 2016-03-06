/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-10 下午5:52:43
 */
package com.absir.aserv.crud;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.aserv.system.helper.HelperQuery;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelList;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JaEntity;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class CrudSupply<T> implements ICrudSupply, IBeanDefineSupply {

	/** entityNameMapClass */
	protected Map<String, Class<? extends T>> entityNameMapClass = new HashMap<String, Class<? extends T>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getEntityNameMapClass()
	 */
	@Override
	public Set<Entry<String, Class<?>>> getEntityNameMapClass() {
		return (Set<Entry<String, Class<?>>>) (Object) entityNameMapClass.entrySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 32;
	}

	/**
	 * @param type
	 * @param beanType
	 */
	protected void put(Class<?> type, Class<?> beanType) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineSupply#getBeanDefines(com.absir.bean
	 * .core.BeanFactoryImpl, java.lang.Class)
	 */
	@Override
	public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
		Class<?> supplyClass = KernelClass.componentClass(getClass());
		if (supplyClass.isAssignableFrom(beanType)) {
			JaEntity jaEntity = beanType.getAnnotation(JaEntity.class);
			if (jaEntity != null || beanType.getAnnotation(MaEntity.class) != null) {
				JaName jaName = beanType.getAnnotation(JaName.class);
				String entityName = jaName == null ? beanType.getSimpleName() : jaName.value();
				Class<?> type = entityNameMapClass.get(entityName);
				if (type == null || beanType.isAssignableFrom(type)) {
					entityNameMapClass.put(entityName, (Class<? extends T>) beanType);
					if (type != null) {
						put(type, beanType);
					}

				} else if (type.isAssignableFrom(beanType)) {
					put(beanType, type);
					if (SessionFactoryUtils.get().getNameMapPermissions().get(entityName) != null) {
						jaEntity = null;
					}

				} else {
					jaEntity = null;
				}

				if (jaEntity != null && jaEntity.permissions().length > 0) {
					SessionFactoryUtils.get().getNameMapPermissions().put(entityName, jaEntity.permissions());
				}

				return KernelLang.NULL_LIST_SET;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getTransactionName()
	 */
	@Override
	public String getTransactionName() {
		return null;
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
		return crud != Crud.COMPLETE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getEntityClass(java.lang.String)
	 */
	@Override
	public Class<? extends T> getEntityClass(String entityName) {
		return entityNameMapClass.get(entityName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierName(java.lang.String)
	 */
	@Override
	public String getIdentifierName(String entityName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.ICrudSupply#getIdentifierType(java.lang.String)
	 */
	@Override
	public Class getIdentifierType(String entityName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#getIdentifier(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Object getIdentifier(String entityName, Object entity) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#find(java.lang.String,
	 * java.lang.Object, com.absir.aserv.jdbc.JdbcCondition)
	 */
	@Override
	public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
		return null;
	}

	/**
	 * @param entityName
	 * @return
	 */
	public Collection findAll(String entityName) {
		return null;
	}

	/**
	 * @param entityName
	 * @param jdbcCondition
	 * @param queue
	 * @return
	 */
	private List list(String entityName, JdbcCondition jdbcCondition, String queue) {
		Collection entities = findAll(entityName);
		if (entities == null) {
			return null;
		}

		Class<?> entityClass = getEntityClass(entityName);
		FilterTemplate<Object> filterTemplate = null;
		if (jdbcCondition != null) {
			List<Object> conditions = jdbcCondition.getConditionList();
			int size = conditions.size();
			if (size > 0) {
				final FilterTemplate filterQuery = HelperQuery.getConditionFilter(entityClass, conditions);
				filterTemplate = new FilterTemplate<Object>() {

					@Override
					public boolean doWith(Object template) throws BreakException {
						return template != null && ((IBase) template).getId() != null && filterQuery.doWith(template);
					}
				};
			}
		}

		return KernelList.getFilterSortList(entities, filterTemplate, HelperQuery.getComparator(entityClass, queue));
	}

	/**
	 * @param xlsBases
	 * @param firstResult
	 * @param maxResults
	 */
	private List list(List xlsBases, int firstResult, int maxResults) {
		int size = xlsBases.size();
		if (firstResult < 0 || firstResult >= size) {
			firstResult = 0;
		}

		maxResults += firstResult;
		if (maxResults < 0 || maxResults > size) {
			maxResults = size;
		}

		return firstResult == 0 && maxResults == 0 ? xlsBases : xlsBases.subList(firstResult, maxResults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#list(java.lang.String,
	 * com.absir.aserv.jdbc.JdbcCondition, java.lang.String, int, int)
	 */
	@Override
	public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
		return list(list(entityName, jdbcCondition, HelperCondition.orderQueue(getEntityClass(entityName), queue)), firstResult, maxResults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#list(java.lang.String,
	 * com.absir.aserv.jdbc.JdbcCondition, java.lang.String,
	 * com.absir.aserv.jdbc.JdbcPage)
	 */
	@Override
	public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
		List entities = list(entityName, jdbcCondition, queue);
		if (entities == null) {
			return null;
		}

		jdbcPage.setTotalCount(entities.size());
		return list(entities, jdbcPage.getFirstResult(), jdbcPage.getPageSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#deleteEntity(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void deleteEntity(String entityName, Object entity) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.crud.ICrudSupply#evict(java.lang.Object)
	 */
	@Override
	public void evict(Object entity) {
	}
}
