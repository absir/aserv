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

@SuppressWarnings({"rawtypes", "serial"})
@Base
@Bean
public class L2CacheCollectionService extends L2EntityMergeService {

    private Map<String, List<MappedByCache>> collectionMappedByCaches = new HashMap<String, List<MappedByCache>>();

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

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        super.onPostDelete(event);
        if (event.getPersister().hasCache()) {
            changeds(event.getSession(), event.getEntity(), collectionMappedByCaches.get(event.getPersister().getEntityName()));
        }
    }

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

    @Override
    public void onPostInsert(PostInsertEvent event) {
        super.onPostInsert(event);
        if (event.getPersister().hasCache()) {
            changeds(event.getSession(), event.getEntity(), collectionMappedByCaches.get(event.getPersister().getEntityName()));
        }
    }

    private void changeds(Session session, Object entity, List<MappedByCache> mappedByCaches) {
        if (mappedByCaches != null) {
            for (MappedByCache mappedByCache : mappedByCaches) {
                changed(session, mappedByCache.mapped.get(entity), mappedByCache);
            }
        }
    }

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

    private static class MappedByCache {

        private int mappedBy;

        private Getter mapped;

        private Getter id;

        private String collectionKey;
    }
}
