/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-10 下午5:52:43
 */
package com.absir.aserv.crud;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.aserv.system.helper.HelperQuery;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelList;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JaEntity;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CrudSupply<T> implements ICrudSupply, IBeanDefineSupply {

    public static final TypeVariable<?> TYPE_VARIABLE = CrudSupply.class.getTypeParameters()[0];

    protected Map<String, Class<? extends T>> entityNameMapClass = new HashMap<String, Class<? extends T>>();

    @Override
    public Set<Entry<String, Class<?>>> getEntityNameMapClass() {
        return (Set<Entry<String, Class<?>>>) (Object) entityNameMapClass.entrySet();
    }

    @Override
    public int getOrder() {
        return 32;
    }

    protected void put(Class<?> type, Class<?> beanType) {
    }

    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        Class<?> supplyClass = KernelClass.typeClass(getClass(), TYPE_VARIABLE);
        if (supplyClass.isAssignableFrom(beanType)) {
            JaEntity jaEntity = BeanConfigImpl.getTypeAnnotation(beanType, JaEntity.class);
            if (jaEntity != null || BeanConfigImpl.getTypeAnnotation(beanType, MaEntity.class) != null) {
                JaName jaName = BeanConfigImpl.getTypeAnnotation(beanType, JaName.class);
                String entityName = jaName == null ? beanType.getSimpleName() : jaName.value();
                Class<?> type = entityNameMapClass.get(entityName);
                if (type == null || beanType.isAssignableFrom(type)) {
                    entityNameMapClass.put(entityName, (Class<? extends T>) beanType);
                    if (type != null) {
                        put(type, beanType);
                    }

                } else if (type.isAssignableFrom(beanType)) {
                    put(beanType, type);
                    if (SessionFactoryUtils.get().getNameMapPermissions().get(entityName) != null) {
                        jaEntity = null;
                    }

                } else {
                    jaEntity = null;
                }

                if (jaEntity != null && jaEntity.permissions().length > 0) {
                    SessionFactoryUtils.get().getNameMapPermissions().put(entityName, jaEntity.permissions());
                }

                return KernelLang.NULL_LIST_SET;
            }
        }

        return null;
    }

    @Override
    public String getTransactionName() {
        return null;
    }

    @Override
    public boolean support(Crud crud) {
        return crud != Crud.COMPLETE;
    }

    @Override
    public Class<? extends T> getEntityClass(String entityName) {
        return entityNameMapClass.get(entityName);
    }

    @Override
    public String getIdentifierName(String entityName) {
        return null;
    }

    @Override
    public Class getIdentifierType(String entityName) {
        return null;
    }

    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return null;
    }

    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        return null;
    }

    public Collection findAll(String entityName) {
        return null;
    }

    private List list(String entityName, JdbcCondition jdbcCondition, String queue) {
        Collection entities = findAll(entityName);
        if (entities == null) {
            return null;
        }

        Class<?> entityClass = getEntityClass(entityName);
        FilterTemplate<Object> filterTemplate = null;
        if (jdbcCondition != null) {
            List<Object> conditions = jdbcCondition.getConditionList();
            int size = conditions.size();
            if (size > 0) {
                final FilterTemplate filterQuery = HelperQuery.getConditionFilter(entityClass, conditions);
                filterTemplate = new FilterTemplate<Object>() {

                    @Override
                    public boolean doWith(Object template) throws BreakException {
                        return template != null && ((IBase) template).getId() != null && filterQuery.doWith(template);
                    }
                };
            }
        }

        return KernelList.getFilterSortList(entities, filterTemplate, HelperQuery.getComparator(entityClass, queue));
    }

    private List list(List xlsBases, int firstResult, int maxResults) {
        int size = xlsBases.size();
        if (firstResult < 0 || firstResult >= size) {
            firstResult = 0;
        }

        maxResults += firstResult;
        if (maxResults < 0 || maxResults > size) {
            maxResults = size;
        }

        return firstResult == 0 && maxResults == 0 ? xlsBases : xlsBases.subList(firstResult, maxResults);
    }

    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        return list(list(entityName, jdbcCondition, HelperCondition.orderQueue(getEntityClass(entityName), queue)), firstResult, maxResults);
    }

    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        List entities = list(entityName, jdbcCondition, queue);
        if (entities == null) {
            return null;
        }

        jdbcPage.setTotalCount(entities.size());
        return list(entities, jdbcPage.getFirstResult(), jdbcPage.getPageSize());
    }

    @Override
    public void deleteEntity(String entityName, Object entity) {
    }

    @Override
    public void evict(Object entity) {
    }

    @Override
    public void flush() {
    }
}
