/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-22 上午10:26:04
 */
package com.absir.orm.hibernate;

import com.absir.aop.AopInterceptorAbstract;
import com.absir.aop.AopProxy;
import com.absir.aop.AopProxyHandler;
import com.absir.aop.AopProxyUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocEntity;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocType;
import com.absir.orm.hibernate.boost.EntityBoost;
import com.absir.orm.hibernate.boost.IEventService;
import com.absir.orm.value.JiRelation;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.BasicType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
@Base
@Bean
public class SessionFactoryBoost {

    @Inject(type = InjectType.Selectable)
    private BasicType[] basicTypes;

    @Inject(type = InjectType.Selectable)
    private IEventService[] eventServices;

    private Object metadata;

    public static ClassMetadata getClassMetadata(SessionFactory sessionFactory, Class<?> entityClass) {
        EntityPersister persister = ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersisters().get(entityClass.getName());
        return persister == null || !(persister instanceof ClassMetadata) ? null : (ClassMetadata) persister;
    }

    public BasicType[] getBasicTypes() {
        return basicTypes;
    }

    public void beforeBuildConfiguration(Configuration configuration, final boolean locale) {
        // EntityBoost.boost(configuration, this, locale);
        final MetadataSources metadataSources = (MetadataSources) KernelObject.declaredGet(configuration,
                "metadataSources");
        Method buildMetadataMethod = KernelReflect.declaredMethod(metadataSources.getClass(), "buildMetadata");
        Method buildMetadataServiceMethod = KernelReflect.declaredMethod(metadataSources.getClass(), "buildMetadata",
                StandardServiceRegistry.class);
        final AopInterceptorAbstract<Object> buildMetadataAopInterceptor = new AopInterceptorAbstract<Object>() {

            @Override
            public Object after(Object proxy, Object returnValue, Object interceptor, AopProxyHandler proxyHandler,
                                Method method, Object[] args, Throwable e) throws Throwable {
                metadata = super.after(proxy, returnValue, interceptor, proxyHandler, method, args, e);
                EntityBoost.boost((MetadataImpl) metadata, SessionFactoryBoost.this, locale);
                return metadata;
            }

        };
        buildMetadataAopInterceptor.getMethodMapInterceptor().put(buildMetadataMethod, Boolean.TRUE);
        buildMetadataAopInterceptor.getMethodMapInterceptor().put(buildMetadataServiceMethod, Boolean.TRUE);
        AopInterceptorAbstract<Object> buildMetadataBuilderAopInterceptor = new AopInterceptorAbstract<Object>() {

            @Override
            public Object after(Object proxy, Object returnValue, Object interceptor, AopProxyHandler proxyHandler,
                                Method method, Object[] args, Throwable e) throws Throwable {
                Object metadataBuilder = super.after(proxy, returnValue, interceptor, proxyHandler, method, args, e);
                Object metadataBuilderProxy = new MetadataBuilderImpl(metadataSources) {

                    @Override
                    public MetadataImplementor build() {
                        MetadataImplementor metadataImplementor = super.build();
                        metadata = metadataImplementor;
                        EntityBoost.boost(metadataImplementor, SessionFactoryBoost.this, locale);
                        return metadataImplementor;
                    }

                };

                KernelObject.clone(metadataBuilder, metadataBuilderProxy);
                return metadataBuilderProxy;
            }

        };

        Method getMetadataBuilderMethod = KernelReflect.declaredMethod(metadataSources.getClass(),
                "getMetadataBuilder");
        Method getMetadataBuilderMethodReg = KernelReflect.declaredMethod(metadataSources.getClass(),
                "getMetadataBuilder", StandardServiceRegistry.class);
        buildMetadataBuilderAopInterceptor.getMethodMapInterceptor().put(getMetadataBuilderMethod, Boolean.TRUE);
        buildMetadataBuilderAopInterceptor.getMethodMapInterceptor().put(getMetadataBuilderMethodReg, Boolean.TRUE);
        AopProxy metadataSourcesProxy = AopProxyUtils.getProxy(metadataSources, false, true);
        metadataSourcesProxy.getAopInterceptors().add(buildMetadataAopInterceptor);
        metadataSourcesProxy.getAopInterceptors().add(buildMetadataBuilderAopInterceptor);
        KernelObject.declaredSet(configuration, "metadataSources", metadataSourcesProxy);
    }

    public void afterBuildConfiguration(Configuration configuration, SessionFactoryImpl sessionFactory) {
        EventListenerRegistry eventListenerRegistry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);
        if (eventServices != null) {
            for (IEventService eventService : eventServices) {
                eventService.setEventListenerRegistry(eventListenerRegistry);
            }
        }

        boost(configuration, SessionFactoryUtils.get(), sessionFactory);
        boost(sessionFactory);
    }

    public void boost(Map<String, PersistentClass> classes, PersistentClass persistentClass, Property property,
                      Field field, String referencedEntityName) {
        if (eventServices != null) {
            for (IEventService eventService : eventServices) {
                eventService.boost(classes, persistentClass, property, field, referencedEntityName);
            }
        }
    }

    protected void boost(Configuration configuration, SessionFactoryBean sessionFactoryBean,
                         SessionFactory sessionFactory) {
        Map<String, PersistentClass> classes = (Map<String, PersistentClass>) KernelObject.declaredGet(metadata,
                "entityBindingMap");
        for (Entry<String, PersistentClass> entry : classes.entrySet()) {
            PersistentClass persistentClass = entry.getValue();
            sessionFactoryBean.getEntityNameMapJpaEntityName().put(persistentClass.getEntityName(),
                    persistentClass.getJpaEntityName());
            Class<?> mappedClass = persistentClass.getMappedClass();
            if (mappedClass != null) {
                sessionFactoryBean.getJpaEntityNameMapEntityClassFactory().put(persistentClass.getJpaEntityName(),
                        new ObjectEntry<Class<?>, SessionFactory>(mappedClass, sessionFactory));
            }
        }

        metadata = null;
    }

    protected void boost(SessionFactoryImpl sessionFactory) {
        Map<String, EntityPersister> classMetadata = new HashMap<String, EntityPersister>();
        for (Entry<String, EntityPersister> entry : sessionFactory.getMetamodel().entityPersisters().entrySet()) {
            classMetadata.put(entry.getKey(), entry.getValue());
            if (!(entry.getValue() instanceof ClassMetadata)) {
                continue;
            }

            String jpaEntityName = SessionFactoryUtils.getJpaEntityName(entry.getKey());
            //下面代码会导致NativeSql无法执行
            //classMetadata.put(jpaEntityName, entry.getValue());
            List<AssocEntity> assocEntities = SessionFactoryUtils.get().getNameMapAssocEntities().get(entry.getKey());
            if (assocEntities != null) {
                String identifierName = entry.getValue().getIdentifierPropertyName();
                Object propertyMap = KernelObject.declaredGet(entry.getValue(), "propertyMapping");
                Map<Object, Object> typesByPropertyPath = (Map<Object, Object>) KernelObject.declaredGet(propertyMap,
                        "typesByPropertyPath");
                Map<Object, Object> columnsByPropertyPath = (Map<Object, Object>) KernelObject.declaredGet(propertyMap,
                        "columnsByPropertyPath");
                Map<Object, Object> formulaTemplatesByPropertyPath = (Map<Object, Object>) KernelObject
                        .declaredGet(propertyMap, "formulaTemplatesByPropertyPath");
                Map<Object, Object> columnReaderTemplatesByPropertyPath = (Map<Object, Object>) KernelObject
                        .declaredGet(propertyMap, "columnReaderTemplatesByPropertyPath");
                for (AssocEntity assocEntity : assocEntities) {
                    String assocEntityName = SessionFactoryUtils.getEntityName(assocEntity.getEntityName());
                    if (JiRelation.class.isAssignableFrom(assocEntity.getEntityClass())) {
                        ClassMetadata relateClassMetadata = sessionFactory.getAllClassMetadata().get(assocEntityName);
                        Object relatePropertyMap = KernelObject.declaredGet(relateClassMetadata, "propertyMapping");
                        Map<Object, Object> relateTypesByPropertyPath = (Map<Object, Object>) KernelObject
                                .declaredGet(relatePropertyMap, "typesByPropertyPath");
                        Map<Object, Object> relateColumnsByPropertyPath = (Map<Object, Object>) KernelObject
                                .declaredGet(relatePropertyMap, "columnsByPropertyPath");
                        Map<Object, Object> relateFormulaTemplatesByPropertyPath = (Map<Object, Object>) KernelObject
                                .declaredGet(relatePropertyMap, "formulaTemplatesByPropertyPath");
                        Map<Object, Object> relateColumnReaderTemplatesByPropertyPath = (Map<Object, Object>) KernelObject
                                .declaredGet(relatePropertyMap, "columnReaderTemplatesByPropertyPath");

                        String relatePropertyName = "$" + assocEntity.getReferenceEntityName();
                        String relateIdentifierName = "relateId";
                        relateTypesByPropertyPath.put(relatePropertyName, new AssocType(null,
                                SessionFactoryUtils.getEntityName(assocEntity.getReferenceEntityName())));
                        relateColumnsByPropertyPath.put(relatePropertyName,
                                relateColumnsByPropertyPath.get(relateIdentifierName));
                        relateFormulaTemplatesByPropertyPath.put(relatePropertyName,
                                relateFormulaTemplatesByPropertyPath.get(relateIdentifierName));
                        relateColumnReaderTemplatesByPropertyPath.put(relatePropertyName,
                                relateColumnReaderTemplatesByPropertyPath.get(relateIdentifierName));
                    }

                    String propertyName = "$" + assocEntity.getEntityName();
                    typesByPropertyPath.put(propertyName, new AssocType(null, assocEntityName, "assocId"));
                    columnsByPropertyPath.put(propertyName, columnsByPropertyPath.get(identifierName));
                    formulaTemplatesByPropertyPath.put(propertyName,
                            formulaTemplatesByPropertyPath.get(identifierName));
                    columnReaderTemplatesByPropertyPath.put(propertyName,
                            columnReaderTemplatesByPropertyPath.get(identifierName));
                }
            }
        }

        classMetadata = Collections.unmodifiableMap(classMetadata);
        KernelObject.declaredSet(sessionFactory.getMetamodel(), "entityPersisterMap", classMetadata);

        //下面代码可能会导致可能会出现多个数据
//        Map<String, EntityPersister> entityPersisters = new HashMap<String, EntityPersister>();
//        for (Entry<String, EntityPersister> entry : sessionFactory.getEntityPersisters().entrySet()) {
//            EntityPersister entityPersister = entry.getValue();
//            entityPersisters.put(entry.getKey(), entityPersister);
//            entityPersisters.put(SessionFactoryUtils.getJpaEntityName(entry.getKey()), entityPersister);
//        }
//
//        entityPersisters = Collections.unmodifiableMap(entityPersisters);
//        KernelObject.declaredSet(sessionFactory, "entityPersisters", entityPersisters);
    }

    public static class MetadataBuilderProxy extends MetadataBuilderImpl {

        public MetadataBuilderProxy(MetadataSources sources) {
            super(sources);
        }

    }
}
