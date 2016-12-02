/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午5:35:23
 */
package com.absir.aserv.system.domain;

import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import com.absir.orm.transaction.TransactionContext;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class DCache<K extends IBase, V> implements IEntityMerge<K> {

    public static final TypeVariable<?> TYPE_VARIABLE = DCache.class.getTypeParameters()[0];

    protected Class<K> entityClass;

    protected String entityName;

    protected Map<Serializable, V> cacheMap;

    protected Map<Serializable, V> cacheMapBuffer;

    public DCache(String entityName) {
        this(null, null);
    }

    public DCache(Class<K> entityClass, String entityName) {
        if (entityClass == null) {
            entityClass = KernelClass.typeClass(getClass(), TYPE_VARIABLE);
        }

        this.entityName = KernelString.isEmpty(entityName) ? entityClass.getSimpleName() : entityName;
        cacheMap = createCacheMap();
        L2EntityMergeService.ME.addEntityMerges(entityClass, this);
    }

    protected Map<Serializable, V> createCacheMap() {
        return new HashMap<Serializable, V>();
    }

    protected Map<Serializable, V> getCacheMap() {
        return cacheMap;
    }

    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    public V getCacheValue(Serializable id) {
        return cacheMap.get(id);
    }

    /**
     * 重载缓存
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

    protected void reloadCacheTransaction() {
        TransactionContext<?> transactionContext = BeanDao.open(null, BeanService.TRANSACTION_READ_ONLY);
        try {
            reloadCache(BeanDao.getSession());

        } finally {
            BeanDao.commit(transactionContext, null);
        }
    }

    @Override
    public void merge(String entityName, K entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType,
                      Object mergeEvent) {
        if (mergeType == MergeType.RELOAD) {
            reloadCacheTransaction();
            return;
        }

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

    protected abstract V getCacheValue(K entity);

}
