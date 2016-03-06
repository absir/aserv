/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.aserv.slave.bean.base.JbBeanLTargets;
import com.absir.aserv.system.helper.HelperArray;

/**
 * @author absir
 *
 */
public class OTargetsActivity<T> {

	/** singleActivity */
	private T singleActivity;

	/** singleActivityMap */
	private Map<Long, T> singleActivityMap = new HashMap<Long, T>();

	/**
	 * @param serverId
	 * @return
	 */
	public T getSingleActivity(long serverId) {
		T activity = singleActivityMap.get(serverId);
		return activity == null ? singleActivity : activity;
	}

	/**
	 * 
	 */
	public void clearActivity() {
		singleActivity = null;
		singleActivityMap.clear();
	}

	/**
	 * @param activity
	 * @return
	 */
	public JbBeanLTargets getTargets(T activity) {
		if (activity == null) {
			return null;
		}

		if (activity instanceof JbBeanLTargets) {
			return (JbBeanLTargets) activity;
		}

		if (activity instanceof Entry) {
			Object key = ((Entry<?, ?>) activity).getKey();
			if (key instanceof JbBeanLTargets) {
				return (JbBeanLTargets) key;
			}
		}

		return null;
	}

	/**
	 * @param oldActivity
	 * @param targets
	 * @return
	 */
	public boolean canOverwrite(T oldActivity, JbBeanLTargets targets) {
		JbBeanLTargets oldTargets = getTargets(oldActivity);
		return oldActivity == null
				|| oldTargets.getTargets() == null
				|| oldTargets.getTargets().length == 0
				|| (targets.getTargets() != null && targets.getTargets().length != 0 && targets.getTargets().length <= oldTargets
						.getTargets().length);
	}

	/**
	 * @param targets
	 * @param activity
	 */
	public void addActivity(JbBeanLTargets targets, T activity) {
		if (targets == null || targets.getTargets() == null || targets.getTargets().length == 0) {
			singleActivity = activity;

		} else {
			for (long targetId : targets.getTargets()) {
				T oldActivity = singleActivityMap.get(targetId);
				if (oldActivity == null || canOverwrite(oldActivity, targets)) {
					singleActivityMap.put(targetId, activity);
				}
			}
		}
	}

	/**
	 * @param targetIds
	 * @param serverId
	 * @return
	 */
	public static boolean isContainTargetId(long[] targetIds, long serverId) {
		return targetIds == null || targetIds.length == 0 || HelperArray.contains(targetIds, serverId);
	}
}
