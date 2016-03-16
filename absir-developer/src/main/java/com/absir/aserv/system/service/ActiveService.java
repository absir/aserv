/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-6 下午3:05:33
 */
package com.absir.aserv.system.service;

import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.system.domain.DActiver;
import com.absir.aserv.system.domain.DActiverMap;
import com.absir.async.value.Async;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.context.core.ContextService;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ActiveService<T extends JiActive, K> extends ContextService implements IEntityMerge<T> {

    public static final TypeVariable<?> TYPE_VARIABLE = ActiveService.class.getTypeParameters()[0];

    protected DActiver<T> activer;

    protected DActiverMap<T, K> activerMap;

    /**
     * 查找关联实体
     *
     * @return
     */
    protected Class<?> findEntityClass() {
        return KernelClass.typeClass(getClass(), TYPE_VARIABLE);
    }

    /**
     * 开始服务
     */
    @Started
    protected void started() {
        if (activer == null) {
            String entityName = SessionFactoryUtils.getEntityNameNull(findEntityClass());
            if (!KernelString.isEmpty(entityName)) {
                activer = new DActiver<T>(entityName);
            }
        }

        if (activerMap == null) {
            activerMap = new ActiveMap();
        }

        if (activer != null) {
            getInstance().reloadActives(ContextUtils.getContextTime());
        }
    }

    protected abstract ActiveService<T, K> getInstance();

    protected Map<Serializable, K> createActiveContexts() {
        return null;
    }

    protected abstract boolean isClosed(K activeContext);

    protected abstract K createActiveContext(T active);

    protected K updateActiveContext(T active, K activeContext) {
        return activeContext;
    }

    protected abstract void closeActiveContext(Serializable id, K activeContext);

    protected void reloadAllActiveContext(boolean hasClosed) {
    }

    @Override
    public void step(long contextTime) {
        if (activer != null && activer.stepNext(contextTime)) {
            getInstance().reloadActives(contextTime);
        }
    }

    @Override
    public void merge(String entityName, T entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType,
                      Object mergeEvent) {
        if (activer != null) {
            activer.merge(entity, mergeType, mergeEvent);
        }
    }

    @Async(notifier = true)
    @Transaction(readOnly = true)
    public void reloadActives(long contextTime) {
        if (activerMap != null) {
            activerMap.setActives(activer.reloadActives(contextTime));
        }
    }

    @Stopping
    public void stopping() {
        if (activerMap != null) {
            for (Entry<Serializable, K> entry : activerMap.getOnlineActiveContexts().entrySet()) {
                getInstance().closeActiveContext(entry.getKey(), entry.getValue());
            }
        }

        activer = null;
        activerMap = null;
    }

    public static abstract class ActiveServiceData<T extends JiActive, K> extends ActiveService<T, K> {

        @Override
        protected boolean isClosed(K activeContext) {
            return false;
        }

        @Override
        protected void closeActiveContext(Serializable id, K activeContext) {
        }
    }

    public static abstract class ActiveServiceSingle<T extends JiActive, K>
            extends ActiveServiceData<T, ObjectEntry<T, K>> {

        private ObjectEntry<T, K> singleEntry;

        public ObjectEntry<T, K> getSingleEntry() {
            return singleEntry;
        }

        @Override
        protected ObjectEntry<T, K> createActiveContext(T active) {
            singleEntry = new ObjectEntry<T, K>(active, createActiveContextSingle(active));
            return singleEntry;
        }

        protected abstract K createActiveContextSingle(T active);

        @Override
        protected ObjectEntry<T, K> updateActiveContext(T active, ObjectEntry<T, K> activeContext) {
            activeContext.setKey(active);
            return activeContext;
        }

        @Override
        protected void closeActiveContext(Serializable id, ObjectEntry<T, K> activeContext) {
            super.closeActiveContext(id, activeContext);
            if (singleEntry != null && singleEntry.getKey().getId().equals(id)) {
                Iterator<Entry<Serializable, ObjectEntry<T, K>>> iterator = activerMap.getOnlineActiveContexts()
                        .entrySet().iterator();
                singleEntry = iterator.hasNext() ? iterator.next().getValue() : null;
            }
        }
    }

    protected class ActiveMap extends DActiverMap<T, K> {

        @Override
        protected Map<Serializable, K> createActiveContexts() {
            return ActiveService.this.createActiveContexts();
        }

        @Override
        protected boolean isClosed(K activeContext) {
            return ActiveService.this.isClosed(activeContext);
        }

        @Override
        protected K createActiveContext(T active) {
            return ActiveService.this.createActiveContext(active);
        }

        @Override
        protected K updateActiveContext(T active, K activeContext) {
            return ActiveService.this.updateActiveContext(active, activeContext);
        }

        @Override
        protected void closeActiveContext(Serializable id, K activeContext) {
            ActiveService.this.closeActiveContext(id, activeContext);
        }

        @Override
        protected void reloadAllActiveContext(boolean hasClosed) {
            ActiveService.this.reloadAllActiveContext(hasClosed);
        }
    }
}
