/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年1月26日 下午1:08:30
 */
package com.absir.aserv.init;

import java.util.List;
import java.util.Map;

public class InitBean {

    // 实体名
    private String entityName;

    // 实体类
    private Class<?> entityClass;

    // 合并字段
    private String[] merges;

    // 实体列表
    private List<Map<String, Object>> beans;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String[] getMerges() {
        return merges;
    }

    public void setMerges(String[] merges) {
        this.merges = merges;
    }

    public List<Map<String, Object>> getBeans() {
        return beans;
    }

    public void setBeans(List<Map<String, Object>> beans) {
        this.beans = beans;
    }

}
