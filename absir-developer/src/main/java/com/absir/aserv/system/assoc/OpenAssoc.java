/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月20日 下午3:53:40
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
public class OpenAssoc implements IAssocDao {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.aserv.system.dao.IAssocDao#supportAssocClass(java.lang.Class,
     * java.lang.String, com.absir.aserv.system.bean.proxy.JiUserBase,
     * com.absir.aserv.support.entity.value.JePermission)
     */
    @Override
    public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase user,
                                     JePermission permission) {
        return user != null;
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
    public void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies,
                                JdbcCondition jdbcCondition, Conditions includeConditions, Conditions excludeConditions) {
        excludeConditions.add(jdbcCondition.getCurrentPropertyAlias() + ".open");
        excludeConditions.add(true);
    }

}
