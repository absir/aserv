/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-6 上午10:42:51
 */
package com.absir.aserv.system.service;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelLang.PropertyFilter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Inject
public interface CrudService {

    public static final CrudService ME = BeanFactoryUtils.get(CrudService.class);

    public ICrudSupply getCrudSupply(String entityName);

    public void merge(String entityName, Map<String, Object> crudRecord, Object entity, ICrudSupply crudSupply, boolean create, JiUserBase user, PropertyFilter filter);

    public Object delete(String entityName, Serializable id, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user);

    public List delete(String entityName, ICrudSupply crudSupply, JdbcCondition jdbcCondition, JiUserBase user);

}
