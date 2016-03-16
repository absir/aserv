/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-22 下午7:37:26
 */
package com.absir.aserv.system.dao.hibernate;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.base.JbRelation;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.IRelateDao;

public class JRelateDaoImpl extends BaseDaoImpl<JbRelation, Long> implements IRelateDao {

    @Override
    public void relateConditions(String rootEntityName, JiUserBase user, Object strategies, String joinAlias, String entityId, JdbcCondition jdbcCondition, Conditions includeConditions,
                                 Conditions excludeConditions, Conditions joinConditions) {
    }
}
