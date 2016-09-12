/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-31 下午4:59:14
 */
package com.absir.aserv.system.service.impl;

import com.absir.aserv.system.bean.JSession;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JbSession;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.service.SecurityService;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;

@SuppressWarnings("unchecked")
@Bean
public class SecurityServiceImpl extends SecurityService {

    @Override
    protected JbSession createSession(JiUserBase userBase, long remember, String address, String agent) {
        return new JSession();
    }

    @Transaction(readOnly = true)
    @Override
    public JiUserBase getUserBase(Long userId) {
        return loadUser(BeanDao.get(BeanDao.getSession(), JUser.class, userId));
    }

    private JUser loadUser(JUser user) {
        if (user != null) {
            user.getUserRoles().isEmpty();
        }

        return user;
    }

    @Transaction(readOnly = true)
    @Override
    public JiUserBase getUserBase(String username, int roleLevel) {
        return loadUser(JUserDao.ME.findByRefUsername(username));
    }

    @Override
    public JiUserBase openUserBase(String username, String password, String platform, String address) {
        return null;
    }
}
