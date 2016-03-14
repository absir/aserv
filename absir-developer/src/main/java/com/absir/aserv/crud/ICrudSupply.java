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

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public interface ICrudSupply {

    /**
     * @return
     */
    public Set<Entry<String, Class<?>>> getEntityNameMapClass();

    /**
     * @return
     */
    public String getTransactionName();

    /**
     * @param crud
     * @return
     */
    public boolean support(Crud crud);

    /**
     * @param entityName
     */
    public Class<?> getEntityClass(String entityName);

    /**
     * @param entityName
     * @return
     */
    public String getIdentifierName(String entityName);

    /**
     * @param entityName
     * @return
     */
    public Class<? extends Serializable> getIdentifierType(String entityName);

    /**
     * @param entityName
     * @param entity
     * @return
     */
    public Object getIdentifier(String entityName, Object entity);

    /**
     * @param entityName
     * @param id
     * @return
     */
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition);

    /**
     * @param entityName
     * @param jdbcCondition
     * @param queue
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults);

    /**
     * @param entityName
     * @param jdbcCondition
     * @param queue
     * @param jdbcPage
     * @return
     */
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage);

    /**
     * @param entityName
     * @return
     */
    public Object create(String entityName);

    /**
     * @param entityName
     * @param entity
     * @param create
     */
    public void mergeEntity(String entityName, Object entity, boolean create);

    /**
     * @param entityName
     * @param entity
     * @return
     */
    public void deleteEntity(String entityName, Object entity);

    /**
     * @param entity
     */
    public void evict(Object entity);

    /**
     */
    public void flush();
}
