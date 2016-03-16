/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 上午9:33:02
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JiAssoc;

public interface IAssocDao {

    /**
     * 支持关联类型
     *
     * @param assocClass
     * @param rootEntityName
     * @param user
     * @param permission
     * @return
     */
    public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase user, JePermission permission);

    /**
     * 生成关联实体条件
     *
     * @param rootEntityName
     * @param user
     * @param permission
     * @param strategies
     * @param jdbcCondition
     * @param includeConditions (或者条件)
     * @param excludeConditions (必需条件)
     */
    public void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies, JdbcCondition jdbcCondition, Conditions includeConditions,
                                Conditions excludeConditions);
}
