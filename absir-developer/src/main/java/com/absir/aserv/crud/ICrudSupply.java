/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-6 下午3:27:50
 */
package com.absir.aserv.crud;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("rawtypes")
public interface ICrudSupply {

    public Set<Entry<String, Class<?>>> getEntityNameMapClass();

    public String getTransactionName();

    public boolean support(Crud crud);

    public Class<?> getEntityClass(String entityName);

    public String getIdentifierName(String entityName);

    public Class<? extends Serializable> getIdentifierType(String entityName);

    public Object getIdentifier(String entityName, Object entity);

    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition);

    public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults);

    public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage);

    public Object create(String entityName);

    public void mergeEntity(String entityName, Object entity, boolean create);

    public void deleteEntity(String entityName, Object entity);

    public void evict(Object entity);

    public void flush();
}
