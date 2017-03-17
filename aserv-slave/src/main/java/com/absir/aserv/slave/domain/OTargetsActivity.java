/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.slave.bean.JServer;
import com.absir.aserv.slave.bean.base.JbServerTargets;
import com.absir.aserv.system.helper.HelperArray;
import com.absir.sockser.SocketSer;
import com.absir.sockser.SockserService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OTargetsActivity<T> {

    private T singleActivity;

    private Map<Long, T> singleActivityMap = new HashMap<Long, T>();

    public static boolean isContainTargetId(long[] targetIds, long serverId) {
        return targetIds == null || targetIds.length == 0 || HelperArray.contains(targetIds, serverId);
    }

    public static boolean isContainTarget(JbServerTargets targets, JServer server) {
        if (targets.isAllServerIds()) {
            String[] groupIds = targets.getGroupIds();
            return groupIds == null || groupIds.length == 0 || HelperArray.contains(groupIds, server.getGroupId());

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
        if (oldTargets == null) {
            return true;
        }

        if (oldTargets.isAllServerIds()) {
            if (targets.isAllServerIds()) {
                int oldLength = oldTargets.getGroupIds() == null ? 0 : oldTargets.getGroupIds().length;
                int length = targets.getGroupIds() == null ? 0 : targets.getGroupIds().length;
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
        if (targets == null || (targets.isAllServerIds() || targets.getGroupIds() == null || targets.getGroupIds().length == 0)) {
            singleActivity = activity;

        } else {
            if (targets.isAllServerIds()) {
                String[] groupIds = targets.getGroupIds();
                for (SocketSer socketSer : SockserService.ME.getOnlineActiveContexts().values()) {
                    JServer server = (JServer) socketSer.getServer();
                    if (ArrayUtils.contains(groupIds, server.getGroupId())) {
                        Long targetId = server.getId();
                        T oldActivity = singleActivityMap.get(targetId);
                        if (oldActivity == null || canOverwrite(oldActivity, targets)) {
                            singleActivityMap.put(targetId, activity);
                        }
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
