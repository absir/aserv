/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-26 下午2:16:20
 */
package com.absir.aserv.system.assoc;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.IAssocDao;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JiAssoc;

/**
 * @author absir
 * 
 */
public class DeveloperAssoc implements IAssocDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.dao.IAssocDao#supportAssocClass(java.lang.Class,
	 * java.lang.String, com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.aserv.support.entity.value.JePermission)
	 */
	@Override
	public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase user, JePermission permission) {
		return user != null && !user.isDeveloper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.dao.IAssocDao#assocConditions(java.lang.String,
	 * com.absir.aserv.system.bean.proxy.JiUserBase,
	 * com.absir.aserv.support.entity.value.JePermission, java.lang.Object,
	 * com.absir.aserv.jdbc.JdbcCondition,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions,
	 * com.absir.aserv.jdbc.JdbcCondition.Conditions)
	 */
	@Override
	public void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies, JdbcCondition jdbcCondition, Conditions includeConditions,
			Conditions excludeConditions) {
		excludeConditions.add(jdbcCondition.getCurrentPropertyAlias() + ".developer");
		excludeConditions.add(false);
	}
}
