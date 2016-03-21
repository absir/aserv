/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-23 下午2:59:01
 */
package com.absir.aserv.system.domain;

import java.util.List;
import java.util.Map;

public class DCondition {

    private long updateTime;

    private boolean strategy;

    private Map<String, List<Object>> mapStrategies;

    private Map<String, List<Object>> createStrategies;

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isStrategy() {
        return strategy;
    }

    public void setStrategy(boolean strategy) {
        this.strategy = strategy;
    }

    public Map<String, List<Object>> getMapStrategies() {
        return mapStrategies;
    }

    public void setMapStrategies(Map<String, List<Object>> mapStrategies) {
        this.mapStrategies = mapStrategies;
    }

    public Map<String, List<Object>> getCreateStrategies() {
        return createStrategies;
    }

    public void setCreateStrategies(Map<String, List<Object>> createStrategies) {
        this.createStrategies = createStrategies;
    }

}
