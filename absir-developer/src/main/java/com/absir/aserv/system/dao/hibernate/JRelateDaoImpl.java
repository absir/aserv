/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-22 下午7:37:26
 */
package com.absir.aserv.system.dao.hibernate;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.base.JbRelation;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.IRelateDao;

/**
 * @author absir
 * 
 */
public class JRelateDaoImpl extends BaseDaoImpl<JbRelation, Long> implements IRelateDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.dao.IRelateDao#relateConditions(java.lang.String
	 * , com.absir.aserv.system.bean.proxy.Proxies.JpUserBase,
	 * java.lang.Object, java.lang.String, java.lang.String,
	 * com.absir.aserv.jdbc.JdbcCondition,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions)
	 */
	@Override
	public void relateConditions(String rootEntityName, JiUserBase user, Object strategies, String joinAlias, String entityId, JdbcCondition jdbcCondition, Conditions includeConditions,
			Conditions excludeConditions, Conditions joinConditions) {
	}
}
