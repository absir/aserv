/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import com.absir.aserv.master.bean.base.JbBeanServers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// 服务开启映射类
public class OServersActivity<T> {

    protected T singleActivity;

    protected Map<Long, T> singleActivityMap = new HashMap<Long, T>();

    public T getSingleActivity(long serverId) {
        T activity = singleActivityMap.get(serverId);
        return activity == null ? singleActivity : activity;
    }

    public void clearActivity() {
        singleActivity = null;
        singleActivityMap.clear();
    }

    protected JbBeanServers getServers(T activity) {
        if (activity == null) {
            return null;
        }

        if (activity instanceof JbBeanServers) {
            return (JbBeanServers) activity;
        }

        if (activity instanceof Entry) {
            Object key = ((Entry<?, ?>) activity).getKey();
            if (key instanceof JbBeanServers) {
                return (JbBeanServers) key;
            }
        }

        return null;
    }

    public boolean couldOverwriteActivity(T activity, JbBeanServers bean) {
        return couldOverwriteServers(getServers(activity), bean);
    }

    public boolean couldOverwriteServers(JbBeanServers servers, JbBeanServers bean) {
        if (servers != null) {
            long[] serverIds = servers.getServerIds();
            if (serverIds != null) {
                int length = serverIds.length;
                if (length > 0) {
                    serverIds = bean.getServerIds();
                    if (serverIds == null || serverIds.length >= length) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void addServersActivity(JbBeanServers servers, T activity) {
        if (servers == null || servers.getServerIds() == null || servers.getServerIds().length == 0) {
            singleActivity = activity;

        } else {
            for (long targetId : servers.getServerIds()) {
                T oldActivity = singleActivityMap.get(targetId);
                if (oldActivity == null || couldOverwriteActivity(oldActivity, servers)) {
                    singleActivityMap.put(targetId, activity);
                }
            }
        }
    }
}
