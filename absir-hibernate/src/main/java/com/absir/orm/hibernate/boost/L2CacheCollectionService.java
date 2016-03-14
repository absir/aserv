/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-5 下午6:13:09
 */
package com.absir.orm.hibernate.boost;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelString;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author absir
 *
 */
@SuppressWarnings({"rawtypes", "serial"})
@Base
@Bean
public class L2CacheCollectionService extends L2EntityMergeService {

    /**
     * collectionMappedByCaches
     */
    private Map<String, List<MappedByCache>> collectionMappedByCaches = new HashMap<String, List<MappedByCache>>();

    /**
     * @param classes
     * @param entityName
     * @param property
     * @param field
     * @param referencedEntityName
     */
    public void boost(Map<String, PersistentClass> classes, PersistentClass persistentClass, Property property, Field field, String referencedEntityName) {
        if (referencedEntityName == null) {
            return;
        }

        if (!(Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType()))) {
            return;
        }

        if (field.getAnnotation(Cache.class) == null) {
            return;
        }

        String mappedBy = getMappedBy(field);
        if (KernelString.isEmpty(mappedBy)) {
            return;
        }

        PersistentClass referencedPersistentClass = classes.get(referencedEntityName);
        Property mappedByProperty = referencedPersistentClass.getProperty(mappedBy);
        if (mappedByProperty == null) {
            return;
        }

        MappedByCache mappedByCache = new MappedByCache();
        mappedByCache.mappedBy = getProperyIndex(mappedByProperty, referencedPersistentClass.getPropertyIterator());
        mappedByCache.mapped = mappedByProperty.getGetter(referencedPersistentClass.getMappedClass());
        mappedByCache.id = persistentClass.getIdentifierProperty().getGetter(persistentClass.getMappedClass());
        mappedByCache.collectionKey = persistentClass.getEntityName() + "." + property.getName();
        List<MappedByCache> mappedByCaches = collectionMappedByCaches.get(referencedEntityName);
        if (mappedByCaches == null) {
            mappedByCaches = new ArrayList<L2CacheCollectionService.MappedByCache>();
            collectionMappedByCaches.put(referencedEntityName, mappedByCaches);
        }

        mappedByCaches.add(mappedByCache);
    }

    /**
     * @param field
     * @return
     */
    private String getMappedBy(Field field) {
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            return oneToMany.mappedBy();
        }

        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (manyToMany != null) {
            return manyToMany.mappedBy();
        }

        return null;
    }

    /**
     * @param property
     * @param properyIterator
     * @return
     */
    private int getProperyIndex(Property property, Iterator properyIterator) {
        int i = 0;
        while (properyIterator.hasNext()) {
            if (property == properyIterator.next()) {
                return i;
            }

            i++;
        }

        return i;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.hibernate.event.spi.PostDeleteEventListener#onPostDelete(org.hibernate
     * .event.spi.PostDeleteEvent)
     */
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        super.onPostDelete(event);
        if (event.getPersister().hasCache()) {
            changeds(event.getSession(), event.getEntity(), collectionMappedByCaches.get(event.getPersister().getEntityName()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.hibernate.event.spi.PostUpdateEventListener#onPostUpdate(org.hibernate
     * .event.spi.PostUpdateEvent)
     */
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        super.onPostUpdate(event);
        if (event.getPersister().hasCache()) {
            List<MappedByCache> mappedByCaches = collectionMappedByCaches.get(event.getPersister().getEntityName());
            if (mappedByCaches == null) {
                return;
            }

            for (int mappedBy : event.getDirtyProperties()) {
                for (MappedByCache mappedByCache : mappedByCaches) {
                    if (mappedByCache.mappedBy == mappedBy) {
                        changed(event.getSession(), event.getOldState()[mappedBy], mappedByCache);
                        changed(event.getSession(), event.getState()[mappedBy], mappedByCache);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.hibernate.event.spi.PostInsertEventListener#onPostInsert(org.hibernate
     * .event.spi.PostInsertEvent)
     */
    @Override
    public void onPostInsert(PostInsertEvent event) {
        super.onPostInsert(event);
        if (event.getPersister().hasCache()) {
            changeds(event.getSession(), event.getEntity(), collectionMappedByCaches.get(event.getPersister().getEntityName()));
        }
    }

    /**
     * @param session
     * @param entity
     * @param mappedByCaches
     */
    private void changeds(Session session, Object entity, List<MappedByCache> mappedByCaches) {
        if (mappedByCaches != null) {
            for (MappedByCache mappedByCache : mappedByCaches) {
                changed(session, mappedByCache.mapped.get(entity), mappedByCache);
            }
        }
    }

    /**
     * @param session
     * @param entity
     * @param mappedByCache
     */
    private void changed(Session session, Object entity, MappedByCache mappedByCache) {
        if (entity == null) {
            return;
        }

        entity = mappedByCache.id.get(entity);
        if (entity == null) {
            return;
        }

        session.getSessionFactory().getCache().evictCollection(mappedByCache.collectionKey, (Serializable) entity);
    }

    /**
     * @author absir
     */
    private static class MappedByCache {

        /**
         * mappedBy
         */
        private int mappedBy;

        /**
         * mapped
         */
        private Getter mapped;

        /**
         * id
         */
        private Getter id;

        /** collectionKey */
        private String collectionKey;
    }
}
