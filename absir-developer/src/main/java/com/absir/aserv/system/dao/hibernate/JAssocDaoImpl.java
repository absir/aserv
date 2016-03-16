/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 上午9:37:49
 */
package com.absir.aserv.system.dao.hibernate;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.base.JbAssoc;
import com.absir.aserv.system.bean.proxy.JiUser;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.IAssocDao;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelCollection;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JiAssoc;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;

import java.util.Collection;
import java.util.List;

@Bean
public class JAssocDaoImpl<T> extends BaseDaoImpl<JbAssoc, Long> implements IAssocDao {

    @Override
    public boolean supportAssocClass(Class<? extends JiAssoc> assocClass, String rootEntityName, JiUserBase userBase, JePermission permission) {
        return userBase != null && assocClass.isAssignableFrom(getBaseClass());
    }

    @Override
    public final void assocConditions(String rootEntityName, JiUserBase user, JePermission permission, Object strategies, JdbcCondition jdbcCondition, Conditions includeConditions,
                                      Conditions excludeConditions) {
        if (strategies == null || !supportStrategy(rootEntityName, user, strategies)) {
            assocConditions(rootEntityName, user, jdbcCondition, includeConditions, excludeConditions);

        } else {
            assocStrategies(rootEntityName, user, strategies, jdbcCondition, includeConditions, excludeConditions);
        }
    }

    protected void assocConditions(String rootEntityName, JiUserBase user, JdbcCondition jdbcCondition, Conditions includeConditions, Conditions excludeConditions) {
        if (JiUser.class.isAssignableFrom(getBaseClass())) {
            if (user == null) {
                throw new ServerException(ServerStatus.NO_LOGIN);
            }

            String alias = jdbcCondition.getPropertyAlias();
            HelperCondition.concatOR(includeConditions, alias + ".userId = ?");
            includeConditions.add(user.getUserId());
        }
    }

    protected boolean supportStrategy(String rootEntityName, JiUserBase user, Object strategies) {
        return strategies instanceof List && ((List<?>) strategies).size() > 0;
    }

    protected void assocStrategies(String rootEntityName, JiUserBase user, Object strategies, JdbcCondition jdbcCondition, Conditions includeConditions, Conditions excludeConditions) {
        HelperCondition.concatOR(includeConditions, jdbcCondition.getPropertyAlias(1) + ".id IN (?)");
        includeConditions.add(KernelCollection.castToArray((Collection<?>) strategies, SessionFactoryUtils.getIdentifierType(null, getBaseClass())));
    }
}
