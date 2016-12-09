/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-23 下午12:55:09
 */
package com.absir.aserv.system.service;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.support.developer.DModel;
import com.absir.aserv.system.bean.JDict;
import com.absir.aserv.system.bean.base.JbRecycleBase;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JpRecycleBase;
import com.absir.aserv.system.bean.value.JaRecycle;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.domain.DCacheOpenValue;
import com.absir.aserv.system.domain.DCondition;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.BeanServiceUtils;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.binder.BinderUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Base
@Bean
public class EntityService {

    public static final EntityService ME = BeanFactoryUtils.get(EntityService.class);

    DCacheOpenValue<String, JDict> dictCache;

    private Map<String, Boolean> entityNameMapRecycle = new HashMap<String, Boolean>();

    public DCacheOpenValue<String, JDict> getDictCache() {
        return dictCache;
    }

    /**
     * 初始化
     */
    @Inject
    protected void inject() {
        dictCache = new DCacheOpenValue<String, JDict>(JDict.class, null);
        dictCache.addEntityMerges();
        ME.reloadCaches();
    }

    /**
     * 重载实体
     */
    @Async(notifier = true)
    @Schedule(cron = "0 30 0 * * *")
    @Transaction(readOnly = true)
    protected void reloadCaches() {
        Session session = BeanDao.getSession();
        dictCache.reloadCache(session);
    }

    public ICrudSupply getCrudSupply(String entityName) {
        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        if (crudSupply == null) {
            throw new ServerException(ServerStatus.ON_ERROR, entityName + " not exists!");
        }

        return crudSupply;
    }

    public Object find(String entityName, ICrudSupply crudSupply, JiUserBase user, Object id) {
        return crudSupply.get(entityName,
                DynaBinder.to(AccessServiceUtils.selectCondition(entityName, user, null),
                        crudSupply.getIdentifierType(entityName)),
                AccessServiceUtils.selectCondition(entityName, user, null));
    }

    public List list(String entityName, ICrudSupply crudSupply, JiUserBase user, DCondition condition, Object[] ids) {
        return crudSupply.list(entityName, AccessServiceUtils.selectCondition(entityName, user, condition,
                CrudServiceUtils.ids(entityName, ids, crudSupply, null)), null, 0, 0);
    }

    public List list(String entityName, ICrudSupply crudSupply, JiUserBase user, DCondition condition,
                     JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        return crudSupply.list(entityName, AccessServiceUtils.selectCondition(entityName,
                crudSupply.getEntityClass(entityName), user, condition, jdbcCondition), queue, firstResult, maxResults);
    }

    public List list(String entityName, ICrudSupply crudSupply, JiUserBase user, DCondition condition,
                     JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        if (queue == null) {
            JoEntity entity = new JoEntity(entityName, crudSupply.getEntityClass(entityName));
            DModel model = CrudUtils.getCrudModel(entity);
            String value = model.getValue();
            if (KernelString.isEmpty(value)) {
                value = null;
            }

            if (model.isDesc() || value != null) {
                queue = "ORDER BY o." + (value == null ? crudSupply.getIdentifierName(entityName) : value) + (model.isDesc() ? " DESC" : " ASC");
            }

            value = model.getQueue();
            if (!KernelString.isEmpty(value)) {
                if (queue == null) {
                    queue = "ORDER BY " + value;

                } else {
                    queue = queue + " " + value;
                }
            }
        }

        return crudSupply.list(entityName, AccessServiceUtils.selectCondition(entityName,
                crudSupply.getEntityClass(entityName), user, condition, jdbcCondition), queue, jdbcPage);
    }

    public Object persist(String entityName, Object entityObject, ICrudSupply crudSupply, JiUserBase user,
                          PropertyFilter filter) {
        return merge(entityName, crudSupply.create(entityName), entityObject, crudSupply, true, user, filter);
    }

    public Object merge(String entityName, Object entity, Object entityObject, ICrudSupply crudSupply, boolean create,
                        JiUserBase user, PropertyFilter filter) {
        if (entityObject == null || !(entityObject instanceof Map)) {
            if (entityObject.getClass() != crudSupply.getEntityClass(entityName)) {
                return null;
            }

            entityObject = BinderUtils.getEntityMap(entityObject);
        }

        Map<String, Object> crudRecord = create ? null
                : CrudUtils.crudRecord(new JoEntity(entityName, entity.getClass()), entity, filter);
        BinderData binderData = new BinderData();
        BinderResult binderResult = binderData.getBinderResult();
        binderResult.setValidation(true);
        binderResult.setPropertyFilter(filter);
        binderData.mapBind((Map) entityObject, entity);
        if (binderResult.hashErrors()) {
            throw new ServerException(ServerStatus.NO_VERIFY, "bind entity " + entityName + ", has errors = " + binderResult.getPropertyErrors());
        }

        CrudService.ME.merge(entityName, crudRecord, entity, crudSupply, create, user, filter);
        return entity;
    }

    private Object getIdentifier(String entityName, String identifierName, Map propertyMap) {
        Object id = propertyMap.get(identifierName);
        if (id == null) {
            throw new ServerException(ServerStatus.NO_PARAM,
                    "can not find " + entityName + " from id = " + identifierName);
        }

        return id;
    }

    private Object find(String entityName, Object id, ICrudSupply crudSupply, JdbcCondition jdbcCondition) {
        Object entity = CrudServiceUtils.find(crudSupply, entityName, id, jdbcCondition);
        if (entity == null) {
            throw new ServerException(ServerStatus.NO_PARAM, "can not find " + entityName + " from id = " + id);
        }

        return entity;
    }

    private Object update(String entityName, String identifierName, Class identifierType, Map propertyMap,
                          ICrudSupply crudSupply, JiUserBase user, PropertyFilter filter, JdbcCondition jdbcCondition) {
        Object id = DynaBinderUtils.to(getIdentifier(entityName, identifierName, propertyMap), identifierType);
        if (id == null) {
            throw new ServerException(ServerStatus.NO_PARAM,
                    "can not update " + entityName + " from id = " + identifierType);
        }

        Object entity = find(entityName, id, crudSupply, jdbcCondition);
        propertyMap.remove(identifierName);
        return merge(entityName, entity, propertyMap, crudSupply, false, user, filter);
    }

    private Object merge(String entityName, String identifierName, Class identifierType, Map propertyMap,
                         ICrudSupply crudSupply, JiUserBase user, PropertyFilter filter, JdbcCondition jdbcCondition) {
        try {
            Object id = DynaBinderUtils.to(getIdentifier(entityName, identifierName, propertyMap), identifierType);
            if (id != null) {
                Object entity = CrudServiceUtils.find(crudSupply, entityName, id, null);
                if (entity == null) {
                    entity = BeanServiceUtils.similarEntity(entityName, propertyMap);
                }

                if (entity != null) {
                    id = crudSupply.getIdentifier(entityName, entity);
                    entity = CrudServiceUtils.find(crudSupply, entityName, id, jdbcCondition);
                    if (entity != null) {
                        return merge(entityName, entity, propertyMap, crudSupply, false, user, filter);
                    }
                }

                return merge(entityName, crudSupply.create(entityName), propertyMap, crudSupply, false, user, filter);
            }

        } catch (ServerException e) {
        }

        return persist(entityName, propertyMap, crudSupply, user, filter);
    }

    public List insert(String entityName, ICrudSupply crudSupply, JiUserBase user, List<?> entityList,
                       PropertyFilter filter) {
        List<Object> entities = new ArrayList<Object>();
        for (Object entityObject : entityList) {
            entities.add(persist(entityName, entityObject, crudSupply, user, filter));
        }

        return entities;
    }

    public Object update(String entityName, ICrudSupply crudSupply, JiUserBase user, Object entityObject,
                         PropertyFilter filter) {
        if (entityObject == null || !(entityObject instanceof Map)) {
            if (entityObject.getClass() != crudSupply.getEntityClass(entityName)) {
                return null;
            }

            entityObject = BinderUtils.getEntityMap(entityObject);
        }

        return update(entityName, crudSupply.getIdentifierName(entityName), crudSupply.getIdentifierType(entityName),
                (Map) entityObject, crudSupply, user, filter,
                AccessServiceUtils.updateCondition(entityName, user, null));
    }

    public List update(String entityName, ICrudSupply crudSupply, JiUserBase user, List<?> entityList,
                       PropertyFilter filter) {
        Class entityClass = null;
        String identifierName = crudSupply.getIdentifierName(entityName);
        Class identifierType = crudSupply.getIdentifierType(entityName);
        JdbcCondition jdbcCondition = AccessServiceUtils.updateCondition(entityName, user, null);
        List<Object> conditions = jdbcCondition == null ? null : new ArrayList<Object>(jdbcCondition.getConditions());
        List<Object> entities = new ArrayList<Object>();
        for (Object entityObject : entityList) {
            if (entityObject == null || !(entityObject instanceof Map)) {
                if (entityClass == null) {
                    entityClass = crudSupply.getEntityClass(entityName);
                }

                if (entityObject.getClass() != entityClass) {
                    continue;
                }

                entityObject = BinderUtils.getEntityMap(entityObject);
            }

            entities.add(update(entityName, identifierName, identifierType, (Map) entityObject, crudSupply, user,
                    filter, jdbcCondition));
            if (jdbcCondition != null) {
                jdbcCondition.setConditions(new ArrayList<Object>(conditions));
            }
        }

        return entities;
    }

    public Object merge(String entityName, ICrudSupply crudSupply, JiUserBase user, Object entityObject,
                        PropertyFilter filter) {
        if (entityObject == null || !(entityObject instanceof Map)) {
            if (entityObject.getClass() != crudSupply.getEntityClass(entityName)) {
                return null;
            }

            entityObject = BinderUtils.getEntityMap(entityObject);
        }

        return merge(entityName, crudSupply.getIdentifierName(entityName), crudSupply.getIdentifierType(entityName),
                (Map) entityObject, crudSupply, user, filter,
                AccessServiceUtils.updateCondition(entityName, user, null));
    }

    public List merge(String entityName, ICrudSupply crudSupply, JiUserBase user, List<?> entityList,
                      PropertyFilter filter) {
        Class entityClass = null;
        String identifierName = crudSupply.getIdentifierName(entityName);
        Class identifierType = crudSupply.getIdentifierType(entityName);
        JdbcCondition jdbcCondition = AccessServiceUtils.updateCondition(entityName, user, null);
        List<Object> conditions = jdbcCondition == null ? null : new ArrayList<Object>(jdbcCondition.getConditions());
        List<Object> entities = new ArrayList<Object>();
        for (Object entityObject : entityList) {
            if (entityObject == null || !(entityObject instanceof Map)) {
                if (entityClass == null) {
                    entityClass = crudSupply.getEntityClass(entityName);
                }

                if (entityObject.getClass() != entityClass) {
                    continue;
                }

                entityObject = BinderUtils.getEntityMap(entityObject);
            }

            entities.add(merge(entityName, identifierName, identifierType, (Map) entityObject, crudSupply, user, filter,
                    jdbcCondition));
            if (jdbcCondition != null) {
                jdbcCondition.setConditions(new ArrayList<Object>(conditions));
            }
        }

        return entities;
    }

    public Object delete(String entityName, ICrudSupply crudSupply, JiUserBase user, Object id) {
        return CrudServiceUtils.delete(crudSupply, entityName, id,
                AccessServiceUtils.deleteCondition(entityName, user, null), user);
    }

    public List delete(String entityName, ICrudSupply crudSupply, JiUserBase user, Object[] ids) {
        return CrudService.ME.delete(entityName, getCrudSupply(entityName), AccessServiceUtils
                .deleteCondition(entityName, user, CrudServiceUtils.ids(entityName, ids, crudSupply, null)), user);
    }

    public List delete(String entityName, ICrudSupply crudSupply, JiUserBase user, JdbcCondition jdbcCondition) {
        return CrudService.ME.delete(entityName, crudSupply,
                AccessServiceUtils.deleteCondition(entityName, user, jdbcCondition), user);
    }

    public boolean getEntityNameRecycle(String entityName) {
        Boolean recyle = entityNameMapRecycle.get(entityName);
        if (recyle == null) {
            synchronized (entityNameMapRecycle) {
                recyle = entityNameMapRecycle.get(entityName);
                if (recyle == null) {
                    Class<?> recycleClass = SessionFactoryUtils.getEntityClass(entityName + JpRecycleBase.RECYCLE);
                    if (recycleClass == null || JbRecycleBase.class.isAssignableFrom(JpRecycleBase.class)) {
                        recyle = false;

                    } else {
                        Class<?> entityClass = SessionFactoryUtils.getEntityClass(entityName);
                        JaRecycle jaRecycle = entityClass == null ? null
                                : KernelClass.fetchAnnotation(entityClass, JaRecycle.class);
                        recyle = !(jaRecycle == null || !jaRecycle.value());
                    }

                    entityNameMapRecycle.put(entityName, recyle);
                }
            }
        }

        return recyle;
    }

    public void changed(String entityName, ICrudSupply crudSupply, JiUserBase user, DCondition condition,
                        Map<String, Object> modelMap) {
        if (!modelMap.containsKey("updateTime")) {
            modelMap.put("updateTime", System.currentTimeMillis());
        }

        // KernelString.capitalize(entityName)
        modelMap.put(entityName, list(entityName, crudSupply, user, condition, null, null, 0, 0));
        if (getEntityNameRecycle(entityName)) {
            entityName += JpRecycleBase.RECYCLE;
            List recycles = list(entityName, getCrudSupply(entityName), user, condition, null, null, 0, 0);
            if (recycles.size() > 0 && recycles.get(0).getClass().getSuperclass() != JbRecycleBase.class) {
                List<JbRecycleBase> recycleBases = recycles;
                recycles = new ArrayList<JbRecycleBase>();
                for (JbRecycleBase recycleBase : recycleBases) {
                    JbRecycleBase recycle = new JbRecycleBase();
                    recycle.setId(recycleBase.getId());
                    recycle.setUpdateTime(recycle.getUpdateTime());
                    recycles.add(recycle);
                }
            }

            modelMap.put(entityName, recycles);
        }
    }

    public void sync(String entityName, ICrudSupply crudSupply, JiUserBase user, DCondition condition,
                     Map<String, Object> modelMap, Map<?, ?> entityMap, PropertyFilter filter) {
        Object entityList = entityMap.get("delete");
        if (entityList != null && entityList instanceof List) {
            delete(entityName, crudSupply, user, (List<?>) entityList);
        }

        entityList = entityMap.get("merge");
        if (entityList != null && entityList instanceof List) {
            merge(entityName, crudSupply, user, (List<?>) entityList, filter);
        }

        entityList = entityMap.get("insert");
        if (entityList != null && entityList instanceof List) {
            insert(entityName, crudSupply, user, (List<?>) entityList, filter);
        }

        entityList = entityMap.get("update");
        if (entityList != null && entityList instanceof List) {
            update(entityName, crudSupply, user, (List<?>) entityList, filter);
        }

        changed(entityName, crudSupply, user, condition, modelMap);
    }

    public void mirror(String entityName, ICrudSupply crudSupply, JiUserBase user, Map<String, Object> modelMap,
                       List<?> entityList, PropertyFilter filter) {
        CrudService.ME.delete(entityName, crudSupply, AccessServiceUtils.deleteCondition(entityName, user, null), user);
        insert(entityName, crudSupply, user, entityList, filter);
    }
}
