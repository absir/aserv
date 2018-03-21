/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-29 下午9:12:16
 */
package com.absir.aserv.system.service;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.system.bean.JMaMenu;
import com.absir.aserv.system.bean.JMenuPermission;
import com.absir.aserv.system.bean.JPermission;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JeVote;
import com.absir.aserv.system.bean.value.JeVotePermission;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.helper.HelperArray;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;

@Bean
public class AuthService {

    public final static AuthService ME = BeanFactoryUtils.get(AuthService.class);

    /**
     * 菜单权限
     *
     * @param ref
     * @param user
     * @return
     */
    @Transaction(readOnly = true)
    public boolean menuPermission(String ref, JiUserBase user) {
        JMenuPermission menuPermission = BeanDao.get(BeanDao.getSession(), JMenuPermission.class, ref);
        if (menuPermission == null) {
            return false;
        }

        if (user == null) {
            // 匿名用户
            if (menuPermission.getForbidIds() != null) {
                if (HelperArray.contains(menuPermission.getForbidIds(), -1L)) {
                    return false;
                }
            }

            if (menuPermission.getAllowIds() != null) {
                if (HelperArray.contains(menuPermission.getAllowIds(), -1L)) {
                    return true;
                }
            }

            return false;
        }

        // 开发者用户
        if (user.isDeveloper()) {
            return true;
        }

        if (menuPermission.getForbidIds() != null) {
            // 所有用户
            if (HelperArray.contains(menuPermission.getForbidIds(), 0L)) {
                return false;
            }

            // 用户角色
            for (JiUserRole userRole : user.userRoles()) {
                if (HelperArray.contains(menuPermission.getForbidIds(), userRole.getId())) {
                    return false;
                }
            }
        }

        if (menuPermission.getAllowIds() != null) {
            // 所有用户
            if (HelperArray.contains(menuPermission.getAllowIds(), 0L)) {
                return true;
            }

            // 用户角色
            for (JiUserRole userRole : user.userRoles()) {
                if (HelperArray.contains(menuPermission.getAllowIds(), userRole.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 统一权限判断
     *
     * @param entityName
     * @param user
     * @param votePermission
     */
    @Transaction(readOnly = true)
    public boolean permission(String entityName, JiUserBase user, JeVotePermission votePermission) {
        JMaMenu maMenu = BeanDao.get(BeanDao.getSession(), JMaMenu.class, entityName);
        if (maMenu == null || maMenu.getPermissions() == null) {
            return false;
        }

        // 匿名用户
        if (user == null) {
            JeVote jeVote = votePermission.getJeVote(maMenu.getPermissions().get(-1L));
            if (jeVote == JeVote.FORBID) {
                return false;
            }

            if (jeVote == JeVote.ALLOW) {
                return true;
            }

            return false;
        }

        // 开发者用户
        if (user.isDeveloper()) {
            return true;
        }

        return permission(maMenu, user, votePermission);
    }

    /**
     * 实体授权判断
     *
     * @param maMenu
     * @param user
     * @param votePermission
     * @return
     */
    private boolean permission(JMaMenu maMenu, JiUserBase user, JeVotePermission votePermission) {
        boolean allow = false;
        // 所有用户
        JPermission permission = maMenu.getPermissions().get(0L);
        if (permission != null) {
            JeVote jeVote = votePermission.getJeVote(permission);
            if (jeVote == JeVote.FORBID) {
                return false;

            } else if (!allow && jeVote == JeVote.ALLOW) {
                allow = true;
            }
        }

        // 用户角色
        for (JiUserRole userRole : user.userRoles()) {
            permission = maMenu.getPermissions().get(userRole.getId());
            if (permission != null) {
                JeVote jeVote = votePermission.getJeVote(permission);
                if (jeVote == JeVote.FORBID) {
                    return false;

                } else if (!allow && jeVote == JeVote.ALLOW) {
                    allow = true;
                }
            }
        }

        return allow;
    }

    /**
     * 组合统一权限
     *
     * @param entityName
     * @param user
     * @param votePermissions
     * @return
     */
    @Transaction(readOnly = true)
    public boolean permissions(String entityName, JiUserBase user, JeVotePermission... votePermissions) {
        JMaMenu maMenu = BeanDao.get(BeanDao.getSession(), JMaMenu.class, entityName);
        if (maMenu == null || maMenu.getPermissions() == null) {
            return false;
        }

        // 匿名用户
        if (user == null) {
            for (JeVotePermission votePermission : votePermissions) {
                JeVote jeVote = votePermission.getJeVote(maMenu.getPermissions().get(-1L));
                if (jeVote == JeVote.FORBID || jeVote != JeVote.ALLOW) {
                    return false;
                }
            }

            return true;
        }

        // 开发者用户
        if (user.isDeveloper()) {
            return true;
        }

        // 用户角色
        for (JeVotePermission votePermission : votePermissions) {
            if (!permission(maMenu, user, votePermission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 统一权限字段
     *
     * @param joEntity
     * @param user
     * @param votePermission
     * @return
     */
    @Transaction(readOnly = true)
    public PropertyFilter permissionFilter(JoEntity joEntity, JiUserBase user, JeVotePermission votePermission) {
        // 插叙删除过滤
        if (votePermission == JeVotePermission.SELECTABLE || votePermission == JeVotePermission.DELETEABLE) {
            if (permission(joEntity.getEntityName(), user, votePermission)) {
                return null;
            }

            throw new ServerException(ServerStatus.ON_DENIED);
        }

        JMaMenu maMenu = BeanDao.get(BeanDao.getSession(), JMaMenu.class, joEntity.getEntityName());
        if (maMenu == null || maMenu.getPermissions() == null) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        PropertyFilter filter = new PropertyFilter();
        if (user == null) {
            // 匿名用户
            permissionFilter(maMenu, -1L, filter);

        } else {
            // 开发者用户
            if (user.isDeveloper()) {
                return null;
            }

            // 所有用户
            permissionFilter(maMenu, 0L, filter);

            // 用户角色
            for (JiUserRole userRole : user.userRoles()) {
                permissionFilter(maMenu, userRole.getId(), filter);
            }
        }

        if (!filter.containExclude("*")) {
            // 锁定字段
            String[] lockeds = CrudUtils.getGroupFields(joEntity, "locked");
            if (lockeds != null && lockeds.length > 0) {
                for (String field : lockeds) {
                    filter.exclude(field);
                }
            }
        }

        return filter;
    }

    protected void permissionFilter(JMaMenu maMenu, Long roleId, PropertyFilter filter) {
        if (filter.containExclude("*")) {
            return;
        }

        JPermission permission = maMenu.getPermissions().get(roleId);
        if (permission != null) {
            if (permission.getForbiddens() != null) {
                for (String field : permission.getForbiddens()) {
                    if (!KernelString.isEmpty(field)) {
                        if ("*".equals(field)) {
                            filter.clearExcludes();
                            filter.clearIncludes();
                            filter.exclude("*");
                            return;
                        }

                        filter.exclude(field);
                    }
                }
            }

            if (filter.containInclude("*")) {
                return;
            }

            if (permission.getAllows() != null) {
                for (String field : permission.getAllows()) {
                    if (!KernelString.isEmpty(field)) {
                        if ("*".equals(field)) {
                            filter.clearIncludes();
                            filter.include("*");
                            break;
                        }

                        filter.include(field);
                    }
                }
            }
        }
    }

}
