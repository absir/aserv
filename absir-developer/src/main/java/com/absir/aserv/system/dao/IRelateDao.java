/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-22 下午7:28:15
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.proxy.JiUserBase;

public interface IRelateDao {

    /**
     * 处理关联关系
     *
     * @param rootEntityName
     * @param user
     * @param strategies
     * @param joinAlias
     * @param entityId
     * @param jdbcCondition
     * @param includeConditions
     * @param excludeConditions
     * @param joinConditions
     */
    public void relateConditions(String rootEntityName, JiUserBase user, Object strategies, String joinAlias, String entityId, JdbcCondition jdbcCondition, Conditions includeConditions,
                                 Conditions excludeConditions, Conditions joinConditions);

}
