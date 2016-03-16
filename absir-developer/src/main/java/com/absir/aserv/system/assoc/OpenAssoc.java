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

public class OpenAssoc implements IAssocDao {

    @Override
    public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase user,
                                     JePermission permission) {
        return user != null;
    }

    @Override
    public void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies,
                                JdbcCondition jdbcCondition, Conditions includeConditions, Conditions excludeConditions) {
        excludeConditions.add(jdbcCondition.getCurrentPropertyAlias() + ".open");
        excludeConditions.add(true);
    }

}
