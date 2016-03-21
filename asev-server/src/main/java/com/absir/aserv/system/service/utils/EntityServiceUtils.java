/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-10 上午10:28:34
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.domain.DCondition;
import com.absir.aserv.system.service.EntityService;
import com.absir.bean.basis.Configure;
import com.absir.server.in.Input;

import java.util.List;

@SuppressWarnings("rawtypes")
@Configure
public abstract class EntityServiceUtils {

    public static Object find(String entityName, Object id, Input input) {
        return find(entityName, SecurityServiceUtils.getUserBase(), id, input);
    }

    public static Object find(String entityName, JiUserBase user, Object id, Input input) {
        return EntityService.ME.find(entityName, EntityService.ME.getCrudSupply(entityName), user, id);
    }

    public static List list(String entityName, JiUserBase user, DCondition condition, Object[] ids, Input input) {
        return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, condition, ids);
    }

    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults, Input input) {
        return list(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition, queue, firstResult, maxResults, input);
    }

    public static List list(String entityName, JiUserBase user, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults, Input input) {
        return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, null, jdbcCondition, queue, firstResult, maxResults);
    }

    public static List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage, Input input) {
        return list(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition, queue, jdbcPage, input);
    }

    public static List list(String entityName, JiUserBase user, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage, Input input) {
        return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, null, jdbcCondition, queue, jdbcPage);
    }

    public static Object delete(String entityName, Object id) {
        return EntityService.ME.delete(entityName, EntityService.ME.getCrudSupply(entityName), SecurityServiceUtils.getUserBase(), id);
    }

    public static List delete(String entityName, Object[] ids) {
        return EntityService.ME.delete(entityName, EntityService.ME.getCrudSupply(entityName), SecurityServiceUtils.getUserBase(), ids);
    }
}
