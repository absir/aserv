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
import com.absir.async.AsyncRunnableNotifier;
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
public abstract class DCache<K extends IBase, V> implements IEntityMerge<K>, AsyncRunnableNotifier.INotifierProxy {

    public static final TypeVariable<?> TYPE_VARIABLE = DCache.class.getTypeParameters()[0];

    public Runnable reloadListener;

    protected Class<K> entityClass;

    protected String entityName;

    protected String reloadHsql;

    protected Map<Serializable, V> cacheMap;

    protected Runnable notifierRunnable;

    protected Runnable reloadCacheRunnable;

    private boolean isAddedEntityMerges;

    public DCache(String entityName) {
        this(null, null);
    }

    public DCache(Class<K> entityClass, String entityName) {
        if (entityClass == null) {
            entityClass = KernelClass.typeClass(getClass(), TYPE_VARIABLE);
        }

        this.entityClass = entityClass;
        this.entityName = KernelString.isEmpty(entityName) ? entityClass.getSimpleName() : entityName;
        cacheMap = createCacheMap();
        reloadHsql = genReloadHsql();
    }

    protected Map<Serializable, V> createCacheMap() {
        return new HashMap<Serializable, V>();
    }

    public Map<Serializable, V> getCacheMap() {
        return cacheMap;
    }

    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    public V getCacheValue(Serializable id) {
        return cacheMap.get(id);
    }

    public void addEntityMerges() {
        if (isAddedEntityMerges) {
            return;
        }

        isAddedEntityMerges = true;
        L2EntityMergeService.ME.addEntityMerges(entityName, entityClass, this);
    }

    protected String genReloadHsql() {
        return "SELECT o FROM " + entityName + " o";
    }

    /**
     * 重载缓存
     */
    public void reloadCache(Session session) {
        Iterator<K> iterator = QueryDaoUtils.createQueryArray(session, reloadHsql).iterate();
        Map<Serializable, V> cacheMapBuffer = createCacheMap();
        while (iterator.hasNext()) {
            K entity = iterator.next();
            V v = getCacheValue(entity);
            if (v != null) {
                cacheMapBuffer.put(entity.getId(), v);
            }
        }

        cacheMap = cacheMapBuffer;
        if (reloadListener != null) {
            reloadListener.run();
        }
    }

    public void reloadCacheTransaction() {
        if (reloadCacheRunnable == null) {
            reloadCacheRunnable = new Runnable() {
                @Override
                public void run() {
                    TransactionContext<?> transactionContext = BeanDao.open(null, BeanService.TRANSACTION_READ_ONLY);
                    try {
                        reloadCache(BeanDao.getSession());

                    } finally {
                        BeanDao.commit(transactionContext, null);
                    }
                }
            };
        }

        AsyncRunnableNotifier.notifierProxyRun(this, reloadCacheRunnable);
    }

    @Override
    public void merge(String entityName, K entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType,
                      Object mergeEvent) {
        reloadCacheTransaction();
    }

    public Runnable getNotifierRunnable() {
        return notifierRunnable;
    }

    public void setNotifierRunnable(Runnable notifierRunnable) {
        this.notifierRunnable = notifierRunnable;
    }

    protected abstract V getCacheValue(K entity);

}
