package com.absir.aserv.system.service;

import com.absir.aserv.system.bean.JRolePermissions;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

@Bean
public class RolePermissionsService {

    public static final RolePermissionsService ME = BeanFactoryUtils.get(RolePermissionsService.class);

    @Transaction(readOnly = true)
    public JRolePermissions getRolePermissions(JbUserRole userRole) {
        Session session = BeanDao.getSession();
        JRolePermissions rolePermissions = BeanDao.get(session, JRolePermissions.class, userRole.getId());
        if (rolePermissions == null) {
            rolePermissions = new JRolePermissions();
        }

        rolePermissions.setId(userRole.getId());
        rolePermissions.setRolename(userRole.getRolename());
        return rolePermissions;
    }

    @Transaction
    public void saveRolePermissions(JRolePermissions rolePermissions) {

    }

}
