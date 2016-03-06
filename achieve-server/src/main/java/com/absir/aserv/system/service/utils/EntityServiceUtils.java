/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-10 上午10:28:34
 */
package com.absir.aserv.system.service.utils;

import java.util.List;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.domain.DCondition;
import com.absir.aserv.system.service.EntityService;
import com.absir.bean.basis.Configure;
import com.absir.server.in.Input;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
@Configure
public abstract class EntityServiceUtils {

	/**
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public static Object find(String entityName, Object id, Input input) {
		return find(entityName, SecurityServiceUtils.getUserBase(), id, input);
	}

	/**
	 * @param entityName
	 * @param user
	 * @param id
	 * @param input
	 * @return
	 */
	public static Object find(String entityName, JiUserBase user, Object id, Input input) {
		return EntityService.ME.find(entityName, EntityService.ME.getCrudSupply(entityName), user, id);
	}

	/**
	 * @param entityName
	 * @param user
	 * @param condition
	 * @param ids
	 * @param input
	 * @return
	 */
	public static List list(String entityName, JiUserBase user, DCondition condition, Object[] ids, Input input) {
		return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, condition, ids);
	}

	/**
	 * @param entityName
	 * @param jdbcCondition
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param input
	 * @return
	 */
	public static List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults, Input input) {
		return list(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition, queue, firstResult, maxResults, input);
	}

	/**
	 * @param entityName
	 * @param user
	 * @param jdbcCondition
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param input
	 * @return
	 */
	public static List list(String entityName, JiUserBase user, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults, Input input) {
		return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, null, jdbcCondition, queue, firstResult, maxResults);
	}

	/**
	 * @param entityName
	 * @param jdbcCondition
	 * @param queue
	 * @param jdbcPage
	 * @param input
	 * @return
	 */
	public static List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage, Input input) {
		return list(entityName, SecurityServiceUtils.getUserBase(), jdbcCondition, queue, jdbcPage, input);
	}

	/**
	 * @param entityName
	 * @param user
	 * @param jdbcCondition
	 * @param queue
	 * @param jdbcPage
	 * @param input
	 * @return
	 */
	public static List list(String entityName, JiUserBase user, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage, Input input) {
		return EntityService.ME.list(entityName, EntityService.ME.getCrudSupply(entityName), user, null, jdbcCondition, queue, jdbcPage);
	}

	/**
	 * @param entityName
	 * @param id
	 * @return
	 */
	public static Object delete(String entityName, Object id) {
		return EntityService.ME.delete(entityName, EntityService.ME.getCrudSupply(entityName), SecurityServiceUtils.getUserBase(), id);
	}

	/**
	 * @param entityName
	 * @param ids
	 * @return
	 */
	public static List delete(String entityName, Object[] ids) {
		return EntityService.ME.delete(entityName, EntityService.ME.getCrudSupply(entityName), SecurityServiceUtils.getUserBase(), ids);
	}
}
