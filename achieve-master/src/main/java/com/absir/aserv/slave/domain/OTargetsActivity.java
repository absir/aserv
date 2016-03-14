/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.base.JbBeanTargets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 *
 */
public class OTargetsActivity<T> {

    /**
     * singleActivity
     */
    protected T singleActivity;

    /**
     * singleActivityMap
     */
    protected Map<Long, T> singleActivityMap = new HashMap<Long, T>();

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
    public JbBeanTargets getTargets(T activity) {
        if (activity == null) {
            return null;
        }

        if (activity instanceof JbBeanTargets) {
            return (JbBeanTargets) activity;
        }

        if (activity instanceof Entry) {
            Object key = ((Entry<?, ?>) activity).getKey();
            if (key instanceof JbBeanTargets) {
                return (JbBeanTargets) key;
            }
        }

        return null;
    }

    /**
     * @param oldActivity
     * @param targets
     * @return
     */
    public boolean canOverwrite(T oldActivity, JbBeanTargets targets) {
        JbBeanTargets oldTargets = getTargets(oldActivity);
        return canOverwriteTargets(oldTargets, targets);
    }

    /**
     * @param oldTargets
     * @param targets
     * @return
     */
    public boolean canOverwriteTargets(JbBeanTargets oldTargets, JbBeanTargets targets) {
        return oldTargets == null
                || oldTargets.getTargets() == null
                || oldTargets.getTargets().length == 0
                || (targets.getTargets() != null && targets.getTargets().length != 0 && targets.getTargets().length <= oldTargets
                .getTargets().length);
    }

    /**
     * @param targets
     * @param activity
     */
    public void addActivity(JbBeanTargets targets, T activity) {
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
}
