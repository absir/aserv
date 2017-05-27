/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午2:55:04
 */
package com.absir.orm.hibernate.boost;

import com.absir.aop.AopProxyUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Started;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilLinked;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge.MergeType;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
@Bean
@Base
public class L2EntityMergeService
        implements IEventService, PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    public static final L2EntityMergeService ME = BeanFactoryUtils.get(L2EntityMergeService.class);

    public static final TypeVariable<?> TYPE_VARIABLE = IEntityMerge.class.getTypeParameters()[0];

    protected static final Logger LOGGER = LoggerFactory.getLogger(L2EntityMergeService.class);

    protected Map<String, UtilLinked<IEntityMerge>> nameMapEntityMerges = new HashMap<String, UtilLinked<IEntityMerge>>();

    @InjectOrder(value = -1)
    @Started
    protected void loadEntityMerges() {
        List<IEntityMerge> entityMerges = BeanFactoryUtils.get().getBeanObjects(IEntityMerge.class);
        if (entityMerges.isEmpty()) {
            return;
        }

        for (IEntityMerge entityMerge : entityMerges) {
            Class<?> entityMergeType = AopProxyUtils.getBeanType(entityMerge);
            Class<?> entityClass = KernelClass.typeClass(entityMergeType, TYPE_VARIABLE);
            if (entityClass != null) {
                LOGGER.info("addEntityMerges " + entityClass + " => " + entityMergeType);
                addEntityMerges(entityClass, entityMerge);
            }
        }

        for (UtilLinked<IEntityMerge> utilLinked : nameMapEntityMerges.values()) {
            utilLinked.sync();
        }
    }

    protected UtilLinked<IEntityMerge> getEntityMerges(String name) {
        UtilLinked<IEntityMerge> entityMergs = nameMapEntityMerges.get(name);
        if (entityMergs == null) {
            synchronized (this) {
                entityMergs = nameMapEntityMerges.get(name);
                if (entityMergs == null) {
                    entityMergs = new UtilLinked<IEntityMerge>();
                    nameMapEntityMerges.put(name, entityMergs);
                }
            }
        }

        return entityMergs;
    }

    public <T> void addEntityMerges(Class<T> entityClass, IEntityMerge<T> entityMerge) {
        addEntityMerges(null, entityClass, entityMerge);
    }

    public <T> void addEntityMerges(String entityName, Class<T> entityClass, IEntityMerge<T> entityMerge) {
        if (entityName == null) {
            entityName = SessionFactoryUtils.getEntityName(entityClass);
            if (entityName == null) {
                for (Entry<String, Entry<Class<?>, SessionFactory>> entry : SessionFactoryUtils.get()
                        .getJpaEntityNameMapEntityClassFactory().entrySet()) {
                    if (entityClass.isAssignableFrom(entry.getValue().getKey())) {
                        getEntityMerges(entry.getValue().getKey().getName()).add(entityMerge);
                    }
                }

                return;
            }

        } else {
            if (!entityClass.isAssignableFrom(SessionFactoryUtils.getEntityClass(entityName))) {
                return;
            }

            entityName = SessionFactoryUtils.getEntityName(entityName);
        }

        getEntityMerges(entityName).add(entityMerge);
    }

    @Override
    public void boost(Map<String, PersistentClass> classes, PersistentClass persistentClass, Property property,
                      Field field, String referencedEntityName) {
    }

    @Override
    public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry) {
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_INSERT, this);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_UPDATE, this);
        eventListenerRegistry.appendListeners(EventType.POST_COMMIT_DELETE, this);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        String entityName = event.getPersister().getEntityName();
        UtilLinked<IEntityMerge> entityMerges = nameMapEntityMerges.get(entityName);
        if (entityMerges != null) {
            entityName = SessionFactoryUtils.getJpaEntityName(entityName);
            for (IEntityMerge entityMerge : entityMerges.getList()) {
                try {
                    entityMerge.merge(entityName, event.getEntity(), MergeType.DELETE, event);

                } catch (Exception e) {
                    LOGGER.error("onPostDelete " + entityName, e);
                }
            }
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String entityName = event.getPersister().getEntityName();
        UtilLinked<IEntityMerge> entityMerges = nameMapEntityMerges.get(entityName);
        if (entityMerges != null) {
            entityName = SessionFactoryUtils.getJpaEntityName(entityName);
            for (IEntityMerge entityMerge : entityMerges.getList()) {
                try {
                    entityMerge.merge(entityName, event.getEntity(), MergeType.UPDATE, event);

                } catch (Exception e) {
                    LOGGER.error("onPostUpdate " + entityName, e);
                }
            }
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String entityName = event.getPersister().getEntityName();
        UtilLinked<IEntityMerge> entityMerges = nameMapEntityMerges.get(entityName);
        if (entityMerges != null) {
            entityName = SessionFactoryUtils.getJpaEntityName(entityName);
            for (IEntityMerge entityMerge : entityMerges.getList()) {
                try {
                    entityMerge.merge(entityName, event.getEntity(), MergeType.INSERT, event);

                } catch (Exception e) {
                    LOGGER.error("onPostInsert " + entityName, e);
                }
            }
        }
    }

    public void reloadAll() {
        for (Entry<String, UtilLinked<IEntityMerge>> entry : nameMapEntityMerges.entrySet()) {
            String entityName = SessionFactoryUtils.getJpaEntityName(entry.getKey());
            for (IEntityMerge entityMerge : entry.getValue().getList()) {
                entityMerge.merge(entityName, null, MergeType.RELOAD, null);
            }
        }
    }
}
