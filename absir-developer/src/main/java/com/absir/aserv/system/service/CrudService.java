/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-6 上午10:42:51
 */
package com.absir.aserv.system.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelLang.PropertyFilter;

/**
 * @author absir
 * 
 */
@SuppressWarnings("rawtypes")
@Inject
public interface CrudService {

	/** ME */
	public static final CrudService ME = BeanFactoryUtils.get(CrudService.class);

	/**
	 * @param entityName
	 * @return
	 */
	public ICrudSupply getCrudSupply(String entityName);

	/**
	 * @param entityName
	 * @param crudRecord
	 * @param entity
	 * @param crudSupply
	 * @param create
	 * @param user
	 * @param filter
	 */
	public void merge(String entityName, Map<String, Object> crudRecord, Object entity, ICrudSupply crudSupply, boolean create, JiUserBase user, PropertyFilter filter);

	/**
	 * @param entityName
	 * @param id
	 * @param crudSupply
	 * @param jdbcCondition
	 * @param user
	 * @return
	 */
	public Object delete(String entityName, Serializable id, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user);

	/**
	 * @param entityName
	 * @param crudSupply
	 * @param jdbcCondition
	 * @param user
	 * @return
	 */
	public List delete(String entityName, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user);

}
