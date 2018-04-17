/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月12日 下午1:10:57
 */
package com.absir.aserv.slave.domain;

import java.util.HashMap;
import java.util.Map;

// 服务开启映射类
public class OServersActivityGroup<T> extends OServersActivity<T> {

    protected Map<String, T> groupActivityMap = new HashMap<String, T>();

    public T getSingleActivityGroup(long serverId, String group) {
        T activity = singleActivityMap.get(serverId);
        if (activity == null && group != null) {
            activity = groupActivityMap.get(group);
        }

        return activity == null ? singleActivity : activity;
    }
}
