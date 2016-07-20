/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月16日 下午12:50:48
 */
package com.absir.aserv.system.service;

import com.absir.aserv.system.bean.value.IUser;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;

@Bean
public abstract class UserService {

    public static final UserService ME = BeanFactoryUtils.get(UserService.class);

    public static String getPasswordEntry(String password, IUser user) {
        return PasswordCrudFactory.getPasswordEncrypt(password, user.getSalt(), user.getSaltCount());
    }

    @Transaction(rollback = Throwable.class)
    public void register(IUser user) {
        user.setSaltCount(PasswordCrudFactory.getSaltCountDefault());
        user.setPassword(getPasswordEntry(user.getPassword(), user));
        BeanDao.getSession().persist(user);
    }

    @Transaction(rollback = Throwable.class)
    public void setPassword(IUser user, String password, String newPassword) {
        if (password != null) {
            if (!getPasswordEntry(password, user).equals(password)) {
                throw new ServerException(ServerStatus.ON_DENIED);
            }
        }

        user.setPassword(getPasswordEntry(newPassword, user));
        BeanDao.getSession().merge(user);
    }
}
