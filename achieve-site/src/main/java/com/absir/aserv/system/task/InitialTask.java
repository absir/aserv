/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-6 下午5:34:44
 */
package com.absir.aserv.system.task;

import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JUserRole;
import com.absir.aserv.system.bean.value.JeUserType;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.core.base.Environment;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.util.HashSet;

@Bean
public class InitialTask {

    @Started
    @Transaction(rollback = Throwable.class)
    protected void started() {
        if (Environment.isDevelop() || InitBeanFactory.ME.isRequireInit()) {
            JUserRole userRole = insertUserRole(1L, "系统管理员");
            insertUserRole(2L, "管理员");
            insertUser("absir", "developer", userRole, true);
            insertUser("admin", "admin888", userRole, false);
        }
    }

    private JUserRole insertUserRole(Long id, String rolename) {
        Session session = BeanDao.getSession();
        JUserRole userRole = BeanDao.get(session, JUserRole.class, id);
        if (userRole == null) {
            userRole = new JUserRole();
            userRole.setId(id);
            userRole.setRolename(rolename);
            userRole = (JUserRole) session.merge(userRole);
        }

        return userRole;
    }

    private void insertUser(String username, String password, JUserRole userRole, boolean developer) {
        JUser user = JUserDao.ME.findByUsername(username);
        if (user == null) {
            user = new JUser();
            user.setUsername(username);
            user.setPasswordBase(password);
            user.setActivation(true);
            user.setUserType(JeUserType.USER_ADMIN);
            if (user.getUserRoles() == null) {
                user.setUserRoles(new HashSet<JUserRole>());
            }

            user.getUserRoles().add(userRole);
            user.setDeveloper(developer);
            CrudServiceUtils.merge("JUser", null, user, true, null, null);
        }
    }
}
