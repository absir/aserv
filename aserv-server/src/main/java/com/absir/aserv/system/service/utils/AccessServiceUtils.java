/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-9 上午10:42:56
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.base.JbPermission;
import com.absir.aserv.system.bean.base.JbStragety;
import com.absir.aserv.system.bean.base.JbSuggest;
import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JiOpen;
import com.absir.aserv.system.domain.DCondition;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JePermission;

import java.util.List;

public abstract class AccessServiceUtils {

    public static JdbcCondition selectCondition(String entityName, JdbcCondition jdbcCondition) {
        return selectCondition(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition);
    }

    public static JdbcCondition selectCondition(String entityName, JiUserBase user, JdbcCondition jdbcCondition) {
        return selectCondition(entityName, user, null, jdbcCondition);
    }

    /**
     * 列表实体条件
     */
    public static JdbcCondition selectCondition(String entityName, JiUserBase user, DCondition condition,
                                                JdbcCondition jdbcCondition) {
        return selectCondition(entityName, SessionFactoryUtils.getEntityClass(entityName), user, condition,
                jdbcCondition);
    }

    public static JdbcCondition selectCondition(String entityName, Class<?> entityClass, JiUserBase user,
                                                DCondition condition, JdbcCondition jdbcCondition) {
        if (entityClass == null) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        if (condition != null) {
            if (condition.getUpdateTime() > 0) {
                if (entityClass != null && JiUpdate.class.isAssignableFrom(entityClass)) {
                    jdbcCondition.getConditions().add("o.updateTime >= ?");
                    jdbcCondition.getConditions().add(condition.getUpdateTime());
                }

                if (condition.getCreateStrategies() != null) {
                    AssocServiceUtils.assocConditions(JbStragety.class, entityName, user, JePermission.SELECT,
                            condition.getCreateStrategies(), jdbcCondition);
                }
            }
        }

        AssocServiceUtils.assocConditions(JbPermission.class, entityName, user, JePermission.SELECT, jdbcCondition);
        if (condition != null && condition.isStrategy()) {
            AssocServiceUtils.assocConditions(JbStragety.class, entityName, user, JePermission.SELECT,
                    condition.getMapStrategies(), jdbcCondition);
        }

        return jdbcCondition;
    }

    public static JdbcCondition updateCondition(String entityName, JdbcCondition jdbcCondition) {
        return updateCondition(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition);
    }

    /**
     * 编辑实体条件
     */
    public static JdbcCondition updateCondition(String entityName, JiUserBase user, JdbcCondition jdbcCondition) {
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        if (entityClass == null) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        AssocServiceUtils.assocConditions(JbPermission.class, entityName, user, JePermission.UPDATE, jdbcCondition);
        return jdbcCondition;
    }

    public static JdbcCondition deleteCondition(String entityName, JdbcCondition jdbcCondition) {
        return deleteCondition(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition);
    }

    /**
     * 删除实体条件
     */
    public static JdbcCondition deleteCondition(String entityName, JiUserBase user, JdbcCondition jdbcCondition) {
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        if (entityClass == null) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        AssocServiceUtils.assocConditions(JbPermission.class, entityName, user, JePermission.DELETE, jdbcCondition);
        return jdbcCondition;
    }

    public static JdbcCondition suggestCondition(String entityName, JdbcCondition jdbcCondition) {
        return suggestCondition(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition);
    }

    /**
     * 建议实体条件
     */
    public static JdbcCondition suggestCondition(String entityName, JiUserBase user, JdbcCondition jdbcCondition) {
        return suggestCondition(entityName, JbSuggest.class, user, jdbcCondition);
    }

    public static JdbcCondition suggestCondition(String entityName, Class<? extends JbPermission> permissionClass,
                                                 JiUserBase user, JdbcCondition jdbcCondition) {
        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
        if (entityClass == null) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        if (JiOpen.class.isAssignableFrom(entityClass)) {
            List<Object> conditions = jdbcCondition.getConditions();
            conditions.add("o.open");
            conditions.add(Boolean.TRUE);
        }

        AssocServiceUtils.assocConditions(permissionClass, entityName, user, JePermission.SELECT, jdbcCondition);
        return jdbcCondition;
    }
}
