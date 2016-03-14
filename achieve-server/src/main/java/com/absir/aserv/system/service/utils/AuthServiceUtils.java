/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-10 下午5:47:05
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeVotePermission;
import com.absir.aserv.system.service.AuthService;
import com.absir.bean.basis.Configure;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 *
 */
@Configure
public abstract class AuthServiceUtils {

    /**
     * 列表权限
     *
     * @param entityName
     * @param user
     * @return
     */
    public static boolean selectPermission(String entityName, JiUserBase user) {
        return AuthService.ME.permission(entityName, user, JeVotePermission.SELECTABLE);
    }

    /**
     * 更新权限
     *
     * @param entityName
     * @param user
     * @return
     */
    public static boolean updatePermission(String entityName, JiUserBase user) {
        return AuthService.ME.permission(entityName, user, JeVotePermission.UPDATABLE);
    }

    /**
     * 插入权限
     *
     * @param entityName
     * @param user
     * @return
     */
    public static boolean insertPermission(String entityName, JiUserBase user) {
        return AuthService.ME.permission(entityName, user, JeVotePermission.INSERTABLE);
    }

    /**
     * 删除权限
     *
     * @param entityName
     * @param user
     * @return
     */
    public static boolean deletePermission(String entityName, JiUserBase user) {
        return AuthService.ME.permission(entityName, user, JeVotePermission.DELETEABLE);
    }

    /**
     * 组合统一权限
     *
     * @param entityName
     * @param user
     * @param permissions
     * @return
     */
    public static boolean permissions(String entityName, JiUserBase user, JePermission... permissions) {
        if (permissions == null) {
            return true;
        }

        int length = permissions.length;
        if (length == 0) {
            return true;
        }

        JeVotePermission[] votePermissions = new JeVotePermission[length];
        for (int i = 0; i < length; i++) {
            votePermissions[i] = JeVotePermission.forPermission(permissions[i]);
        }

        return AuthService.ME.permissions(entityName, user, votePermissions);
    }

    /**
     * 搜索功能过滤
     *
     * @param joEntity
     * @param user
     * @return
     */
    public static PropertyFilter selectPropertyFilter(JoEntity joEntity, JiUserBase user) {
        return AuthService.ME.permissionFilter(joEntity, user, JeVotePermission.SELECTABLE);
    }

    /**
     * @param entityName
     * @param crudSupply
     * @param user
     * @return
     */
    public static PropertyFilter selectPropertyFilter(String entityName, ICrudSupply crudSupply, JiUserBase user) {
        return selectPropertyFilter(new JoEntity(entityName, crudSupply.getEntityClass(entityName)), user);
    }

    /**
     * 更新数据过滤
     *
     * @param joEntity
     * @param user
     * @return
     */
    public static PropertyFilter updatePropertyFilter(JoEntity joEntity, JiUserBase user) {
        return AuthService.ME.permissionFilter(joEntity, user, JeVotePermission.UPDATABLE);
    }

    /**
     * @param entityName
     * @param crudSupply
     * @param user
     * @return
     */
    public static PropertyFilter updatePropertyFilter(String entityName, ICrudSupply crudSupply, JiUserBase user) {
        return updatePropertyFilter(new JoEntity(entityName, crudSupply.getEntityClass(entityName)), user);
    }

    /**
     * 插入数据过滤
     *
     * @param joEntity
     * @param user
     * @return
     */
    public static PropertyFilter insertPropertyFilter(JoEntity joEntity, JiUserBase user) {
        return AuthService.ME.permissionFilter(joEntity, user, JeVotePermission.INSERTABLE);
    }

    /**
     * @param entityName
     * @param crudSupply
     * @param user
     * @return
     */
    public static PropertyFilter insertPropertyFilter(String entityName, ICrudSupply crudSupply, JiUserBase user) {
        return insertPropertyFilter(new JoEntity(entityName, crudSupply.getEntityClass(entityName)), user);
    }

    /**
     * 删除实体过滤
     *
     * @param joEntity
     * @param user
     */
    public static void deletePropertyFilter(JoEntity joEntity, JiUserBase user) {
        AuthService.ME.permissionFilter(joEntity, user, JeVotePermission.DELETEABLE);
    }

    /**
     * @param entityName
     * @param crudSupply
     * @param user
     * @return
     */
    public static void deletePropertyFilter(String entityName, ICrudSupply crudSupply, JiUserBase user) {
        deletePropertyFilter(new JoEntity(entityName, crudSupply.getEntityClass(entityName)), user);
    }
}
