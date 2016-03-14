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

/**
 * @author absir
 */
public class InitBean {

    // 实体名
    private String entityName;

    // 实体类
    private Class<?> entityClass;

    // 合并字段
    private String[] merges;

    // 实体列表
    private List<Map<String, Object>> beans;

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the entityClass
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the merges
     */
    public String[] getMerges() {
        return merges;
    }

    /**
     * @param merges the merges to set
     */
    public void setMerges(String[] merges) {
        this.merges = merges;
    }

    /**
     * @return the beans
     */
    public List<Map<String, Object>> getBeans() {
        return beans;
    }

    /**
     * @param beans the beans to set
     */
    public void setBeans(List<Map<String, Object>> beans) {
        this.beans = beans;
    }

}
