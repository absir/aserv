/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年7月10日 下午3:22:43
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.slave.bean.base.JbBeanLTargets;
import com.absir.aserv.slave.domain.OTargetsActivity;
import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.system.service.ActiveService.ActiveServiceData;
import com.absir.core.kernel.KernelLang.ObjectEntry;

/**
 * @author absir
 *
 */
public abstract class ActiveServiceTargets<T extends JiActive, K> extends ActiveServiceData<T, ObjectEntry<T, K>> {

	/**
	 * @param active
	 * @return
	 */
	public JbBeanLTargets getBeanLTargets(T active) {
		return (JbBeanLTargets) active;
	}

	/** targetsActivity */
	private OTargetsActivity<ObjectEntry<T, K>> targetsActivity = new OTargetsActivity<ObjectEntry<T, K>>();

	/**
	 * @param serverId
	 * @return
	 */
	public ObjectEntry<T, K> getSingleEntry(long serverId) {
		return targetsActivity.getSingleActivity(serverId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.ActiveService#createActiveContext
	 * (com.absir.aserv.system.bean.value.JiActive)
	 */
	@Override
	protected ObjectEntry<T, K> createActiveContext(T active) {
		ObjectEntry<T, K> entry = new ObjectEntry<T, K>(active, createActiveContextSingle(active));
		addObjectEntry(targetsActivity, active, entry);
		return entry;
	}

	/**
	 * @param activity
	 * @param active
	 * @param entry
	 */
	public void addObjectEntry(OTargetsActivity<ObjectEntry<T, K>> activity, T active, ObjectEntry<T, K> entry) {
		activity.addActivity(getBeanLTargets(entry.getKey()), entry);
	}

	protected abstract K createActiveContextSingle(T active);

	/**
	 * @param active
	 */
	protected void updateActiverMap() {
		OTargetsActivity<ObjectEntry<T, K>> activity = new OTargetsActivity<ObjectEntry<T, K>>();
		for (ObjectEntry<T, K> entry : activerMap.getOnlineActiveContexts().values()) {
			addObjectEntry(activity, entry.getKey(), entry);
		}

		targetsActivity = activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.ActiveService#updateActiveContext
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
	 * com.absir.aserv.system.service.ActiveService#reloadAllActiveContext
	 * (boolean)
	 */
	@Override
	protected void reloadAllActiveContext(boolean hasClosed) {
		super.reloadAllActiveContext(hasClosed);
		updateActiverMap();
	}
}
