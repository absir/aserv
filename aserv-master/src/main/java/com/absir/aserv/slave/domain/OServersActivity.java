/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.master.bean.base.JbServerTargets;
import com.absir.aserv.master.service.MasterSyncService;
import com.absir.aserv.system.helper.HelperArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// 服务开启映射类
public class OServersActivity<T> {

    protected T singleActivity;

    protected Map<Long, T> singleActivityMap = new HashMap<Long, T>();

    public static boolean isContainTargetId(long[] targetIds, long serverId) {
        return targetIds == null || targetIds.length == 0 || HelperArray.contains(targetIds, serverId);
    }

    public static boolean isContainTarget(JbServerTargets targets, JSlaveServer server) {
        if (targets.isAllServerIds()) {
            String[] groups = targets.getGroups();
            return groups == null || groups.length == 0 || HelperArray.contains(groups, server.getGroup());

        } else {
            long[] targetIds = targets.getServerIds();
            return targetIds != null && targetIds.length > 0 && HelperArray.contains(targetIds, server.getId());
        }
    }

    public T getSingleActivity(long serverId) {
        T activity = singleActivityMap.get(serverId);
        return activity == null ? singleActivity : activity;
    }

    public void clearActivity() {
        singleActivity = null;
        singleActivityMap.clear();
    }

    public JbServerTargets getTargets(T activity) {
        if (activity == null) {
            return null;
        }

        if (activity instanceof JbServerTargets) {
            return (JbServerTargets) activity;
        }

        if (activity instanceof Entry) {
            Object key = ((Entry<?, ?>) activity).getKey();
            if (key instanceof JbServerTargets) {
                return (JbServerTargets) key;
            }
        }

        return null;
    }

    public boolean canOverwrite(T oldActivity, JbServerTargets targets) {
        JbServerTargets oldTargets = getTargets(oldActivity);
        return canOverwriteTargets(oldTargets, targets);
    }

    public boolean canOverwriteTargets(JbServerTargets oldTargets, JbServerTargets targets) {
        if (oldTargets == null) {
            return true;
        }

        if (oldTargets.isAllServerIds()) {
            if (targets.isAllServerIds()) {
                int oldLength = oldTargets.getGroups() == null ? 0 : oldTargets.getGroups().length;
                int length = targets.getGroups() == null ? 0 : targets.getGroups().length;
                return oldLength >= length;
            }

            return true;
        }

        if (targets.isAllServerIds()) {
            return false;
        }

        int oldLength = oldTargets.getServerIds() == null ? 0 : oldTargets.getServerIds().length;
        int length = targets.getServerIds() == null ? 0 : targets.getServerIds().length;
        return oldLength >= length;
    }

    public void addActivity(JbServerTargets targets, T activity) {
        if (targets == null || (targets.isAllServerIds() || targets.getGroups() == null || targets.getGroups().length == 0)) {
            singleActivity = activity;

        } else {
            if (targets.isAllServerIds()) {
                for (JSlaveServer server : MasterSyncService.ME.getSlaveServersFromGroups(targets.getGroups())) {
                    Long targetId = server.getId();
                    T oldActivity = singleActivityMap.get(targetId);
                    if (oldActivity == null || canOverwrite(oldActivity, targets)) {
                        singleActivityMap.put(targetId, activity);
                    }
                }

            } else {
                for (long targetId : targets.getServerIds()) {
                    T oldActivity = singleActivityMap.get(targetId);
                    if (oldActivity == null || canOverwrite(oldActivity, targets)) {
                        singleActivityMap.put(targetId, activity);
                    }
                }
            }
        }
    }
}
