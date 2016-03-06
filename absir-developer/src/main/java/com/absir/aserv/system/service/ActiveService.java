/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-5-6 下午3:05:33
 */
package com.absir.aserv.system.service;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

/**
 * @author absir
 * 
 */
public abstract class ActiveService<T extends JiActive, K> extends ContextService implements IEntityMerge<T> {

	/** TYPE_VARIABLE */
	public static final TypeVariable<?> TYPE_VARIABLE = ActiveService.class.getTypeParameters()[0];

	/** activer */
	protected DActiver<T> activer;

	/** activerMap */
	protected DActiverMap<T, K> activerMap;

	/**
	 * @author absir
	 *
	 * @param <T>
	 * @param <K>
	 */
	public static abstract class ActiveServiceData<T extends JiActive, K> extends ActiveService<T, K> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.service.ActiveService#isClosed(java.lang
		 * .Object)
		 */
		@Override
		protected boolean isClosed(K activeContext) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.service.ActiveService#closeActiveContext
		 * (java.io.Serializable, java.lang.Object)
		 */
		@Override
		protected void closeActiveContext(Serializable id, K activeContext) {
		}
	}

	public static abstract class ActiveServiceSingle<T extends JiActive, K>
			extends ActiveServiceData<T, ObjectEntry<T, K>> {

		/** singleEntry */
		private ObjectEntry<T, K> singleEntry;

		/**
		 * @return the singleEntry
		 */
		public ObjectEntry<T, K> getSingleEntry() {
			return singleEntry;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.service.ActiveService#createActiveContext
		 * (com.absir.aserv.system.bean.value.JiActive)
		 */
		@Override
		protected ObjectEntry<T, K> createActiveContext(T active) {
			singleEntry = new ObjectEntry<T, K>(active, createActiveContextSingle(active));
			return singleEntry;
		}

		protected abstract K createActiveContextSingle(T active);

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.service.ActiveService#updateActiveContext
		 * (com.absir.aserv.system.bean.value.JiActive, java.lang.Object)
		 */
		@Override
		protected ObjectEntry<T, K> updateActiveContext(T active, ObjectEntry<T, K> activeContext) {
			activeContext.setKey(active);
			return activeContext;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.service.ActiveService.ActiveServiceData#
		 * closeActiveContext(java.io.Serializable, java.lang.Object)
		 */
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

	/**
	 * @author absir
	 * 
	 */
	protected class ActiveMap extends DActiverMap<T, K> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#createActiveContexts()
		 */
		@Override
		protected Map<Serializable, K> createActiveContexts() {
			return ActiveService.this.createActiveContexts();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#isClosed(com.absir.core
		 * .base.IBase)
		 */
		@Override
		protected boolean isClosed(K activeContext) {
			return ActiveService.this.isClosed(activeContext);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#createActiveContext(com
		 * .absir.core.base.IBase)
		 */
		@Override
		protected K createActiveContext(T active) {
			return ActiveService.this.createActiveContext(active);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#updateActiveContext(com
		 * .absir.core.base.IBase, java.lang.Object)
		 */
		@Override
		protected K updateActiveContext(T active, K activeContext) {
			return ActiveService.this.updateActiveContext(active, activeContext);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#closeActiveContext(java
		 * .lang.Object)
		 */
		@Override
		protected void closeActiveContext(Serializable id, K activeContext) {
			ActiveService.this.closeActiveContext(id, activeContext);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.domain.DActiverMap#reloadAllActiveContext
		 * (boolean)
		 */
		@Override
		protected void reloadAllActiveContext(boolean hasClosed) {
			ActiveService.this.reloadAllActiveContext(hasClosed);
		}
	}

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

	/**
	 * @return
	 */
	protected abstract ActiveService<T, K> getInstance();

	/**
	 * @return
	 */
	protected Map<Serializable, K> createActiveContexts() {
		return null;
	}

	/**
	 * @param activeContext
	 * @return
	 */
	protected abstract boolean isClosed(K activeContext);

	/**
	 * @param active
	 * @return
	 */
	protected abstract K createActiveContext(T active);

	/**
	 * @param active
	 * @param activeContext
	 * @return
	 */
	protected K updateActiveContext(T active, K activeContext) {
		return activeContext;
	}

	/**
	 * @param id
	 * @param activeContext
	 */
	protected abstract void closeActiveContext(Serializable id, K activeContext);

	/**
	 * @param hasClosed
	 */
	protected void reloadAllActiveContext(boolean hasClosed) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.context.core.ContextService#step(long)
	 */
	@Override
	public void step(long contextTime) {
		if (activer != null && activer.stepNext(contextTime)) {
			getInstance().reloadActives(contextTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.orm.hibernate.boost.IEntityMerge#merge(java.lang.String,
	 * java.lang.Object, com.absir.orm.hibernate.boost.IEntityMerge.MergeType,
	 * java.lang.Object)
	 */
	@Override
	public void merge(String entityName, T entity, com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType,
			Object mergeEvent) {
		if (activer != null) {
			activer.merge(entity, mergeType, mergeEvent);
		}
	}

	/**
	 * @param contextTime
	 */
	@Async(notifier = true)
	@Transaction(readOnly = true)
	public void reloadActives(long contextTime) {
		if (activerMap != null) {
			activerMap.setActives(activer.reloadActives(contextTime));
		}
	}

	/**
	 * 
	 */
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
}
