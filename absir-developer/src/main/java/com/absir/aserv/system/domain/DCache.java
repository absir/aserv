/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 下午5:35:23
 */
package com.absir.aserv.system.domain;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Session;

import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;

/**
 * @author absir
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class DCache<K extends IBase, V> implements IEntityMerge<K> {

	/** TYPE_VARIABLE */
	public static final TypeVariable<?> TYPE_VARIABLE = DCache.class.getTypeParameters()[0];

	/** entityClass */
	protected Class<K> entityClass;

	/** entityName */
	protected String entityName;

	/** cacheMap */
	protected Map<Serializable, V> cacheMap;

	/** cacheMapBuffer */
	protected Map<Serializable, V> cacheMapBuffer;

	/**
	 * @param entityName
	 */
	public DCache(String entityName) {
		this(null, null);
	}

	/**
	 * @param entityClass
	 * @param entityName
	 */
	public DCache(Class<K> entityClass, String entityName) {
		if (entityClass == null) {
			entityClass = KernelClass.typeClass(getClass(), TYPE_VARIABLE);
		}

		this.entityName = KernelString.isEmpty(entityName) ? entityClass.getSimpleName() : entityName;
		cacheMap = createCacheMap();
		L2EntityMergeService.ME.addEntityMerges(entityClass, this);
	}

	/**
	 * @return
	 */
	protected Map<Serializable, V> createCacheMap() {
		return new HashMap<Serializable, V>();
	}

	/**
	 * @return the cacheMap
	 */
	protected Map<Serializable, V> getCacheMap() {
		return cacheMap;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cacheMap.isEmpty();
	}

	/**
	 * @param id
	 * @return
	 */
	public V getCacheValue(Serializable id) {
		return cacheMap.get(id);
	}

	/**
	 * 重载缓存
	 * 
	 * @param session
	 */
	public void reloadCache(Session session) {
		Iterator<K> iterator = QueryDaoUtils.createQueryArray(session, "SELECT o FROM " + entityName + " o").iterate();
		cacheMapBuffer = createCacheMap();
		while (iterator.hasNext()) {
			K entity = iterator.next();
			V v = getCacheValue(entity);
			if (v != null) {
				cacheMapBuffer.put(entity.getId(), v);
			}
		}

		cacheMap = cacheMapBuffer;
		cacheMapBuffer = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.orm.hibernate.boost.IEntityMerge#merge(java.lang.String,
	 * java.lang.Object, com.absir.orm.hibernate.boost.IEntityMerge.MergeType,
	 * java.lang.Object)
	 */
	@Override
	public void merge(String entityName, K entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType,
			Object mergeEvent) {
		Serializable id = entity.getId();
		if (id != null) {
			if (mergeType == null) {
				cacheMap.remove(id);
				if (cacheMapBuffer != null) {
					cacheMapBuffer.remove(id);
				}
			}

			V v = getCacheValue(entity);
			if (v == null) {
				cacheMap.remove(id);
				if (cacheMapBuffer != null) {
					cacheMapBuffer.remove(id);
				}

			} else {
				cacheMap.put(id, v);
				if (cacheMapBuffer != null) {
					cacheMapBuffer.put(id, v);
				}
			}
		}
	}

	/**
	 * @param entity
	 * @return
	 */
	protected abstract V getCacheValue(K entity);

}
