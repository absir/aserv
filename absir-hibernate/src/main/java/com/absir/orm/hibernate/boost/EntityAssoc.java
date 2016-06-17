/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.orm.hibernate.boost;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryBoost;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.*;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.*;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.TypeFactory.TypeScope;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("unchecked")
public class EntityAssoc {

    /**
     * 获取属性关联实体名
     *
     * @param value
     * @return
     */
    public static String getReferencedEntityName(Value value) {
        if (value instanceof ToOne) {
            return ((ToOne) value).getReferencedEntityName();

        } else if (value instanceof OneToMany) {
            return ((OneToMany) value).getReferencedEntityName();

        } else if (value instanceof Collection) {
            return getReferencedEntityName(((Collection) value).getElement());
        }

        return null;
    }

    /**
     * 添加分析实体关联入口
     *
     * @param entityName
     * @param assocName
     * @param assocJpaName
     * @param jaAssoc
     */
    protected static void addPersistentClass(String entityName, String assocName, String assocJpaName, JaAssoc jaAssoc) {
        if (JiAssoc.class.isAssignableFrom(jaAssoc.entityClass())) {
            List<AssocEntity> assocEntities = SessionFactoryUtils.get().getNameMapAssocEntities().get(entityName);
            if (assocEntities == null) {
                assocEntities = new ArrayList<AssocEntity>();
                SessionFactoryUtils.get().getNameMapAssocEntities().put(entityName, assocEntities);

            } else {
                for (AssocEntity assocEntity : assocEntities) {
                    if (assocName.equals(assocEntity.entityName)) {
                        return;
                    }
                }
            }

            AssocEntity assocEntity = new AssocEntity(assocName, jaAssoc);
            assocEntities.add(assocEntity);
            SessionFactoryUtils.get().getNameMapEntityAssocEntity().put(assocName, new EntityAssocEntity(entityName, assocEntity));
        }

        SessionFactoryUtils.setEntityPermissions(assocJpaName, jaAssoc.permissions());
    }

    /**
     * 添加分析实体自身
     *
     * @param entityName
     * @param jpaEntityName
     * @param jaEntity
     * @param classes
     * @param sessionFactoryBoost
     */
    protected static void addPersistentClasses(String entityName, String jpaEntityName, JaEntity jaEntity, Map<String, PersistentClass> classes, SessionFactoryBoost sessionFactoryBoost) {
        if (SessionFactoryUtils.get().getEntityNameMapJpaEntityName().containsKey(entityName)) {
            return;
        }

        SessionFactoryUtils.get().getEntityNameMapJpaEntityName().put(entityName, jpaEntityName);
        if (jaEntity != null) {
            SessionFactoryUtils.setEntityPermissions(jpaEntityName, jaEntity.permissions());

            for (JaPermission jaPermission : jaEntity.jaPermissions()) {
                String assocName = entityName + jaPermission.entityName();
                PersistentClass persistentClass = classes.get(assocName);
                if (persistentClass != null && !SessionFactoryUtils.get().getNameMapPermissions().containsKey(entityName)) {
                    SessionFactoryUtils.setEntityPermissions(persistentClass.getJpaEntityName(), jaPermission.permissions());
                }
            }
        }

        PersistentClass persistentClass = classes.get(entityName);
        for (Iterator<Property> iterator = persistentClass.getPropertyClosureIterator(); iterator.hasNext(); ) {
            Property property = iterator.next();
            Field field = KernelReflect.declaredField(persistentClass.getMappedClass(), property.getName());
            if (field == null) {
                continue;
            }

            Value value = property.getValue();
            String referencedEntityName = getReferencedEntityName(value);

            // 属性增强
            sessionFactoryBoost.boost(classes, persistentClass, property, field, referencedEntityName);

            if (Map.class.isAssignableFrom(field.getType())) {
                continue;
            }

            JaField jaField = BeanConfigImpl.getFieldAnnotation(field, JaField.class);
            if (jaField == null || jaField.assocClasses().length <= 0) {
                continue;
            }

            if (referencedEntityName == null) {
                referencedEntityName = jaField.referenceEntityName();
                if (KernelString.isEmpty(referencedEntityName)) {
                    JaNames jaNames = BeanConfigImpl.getFieldAnnotation(field, JaNames.class);
                    if (jaNames != null && KernelString.isEmpty(jaNames.value())) {
                        referencedEntityName = jaNames.value();
                    }
                }

                Class<?> referencedEntityClass = jaField.referencEntityClass();
                if (referencedEntityClass == null || referencedEntityClass == void.class) {
                    JaClasses jaClasses = BeanConfigImpl.getFieldAnnotation(field, JaClasses.class);
                    if (jaClasses != null && jaClasses.value() != void.class) {
                        referencedEntityClass = jaClasses.value();
                    }
                }

                if (field.getType().isArray() || java.util.Collection.class.isAssignableFrom(field.getType())) {
                    addEntityAssocField(Referenced.Referenes, entityName, referencedEntityName, referencedEntityClass, field, jaField);

                } else {
                    addEntityAssocField(Referenced.Referene, entityName, referencedEntityName, referencedEntityClass, field, jaField);
                }

            } else {
                if (value instanceof Collection) {
                    if (((Collection) value).getElement() instanceof OneToMany) {
                        addEntityAssocField(Referenced.ToMany, referencedEntityName, entityName, field, jaField);

                    } else {
                        addEntityAssocField(Referenced.Collection, entityName, referencedEntityName, field, jaField);
                    }

                } else {
                    if (value instanceof ToOne) {
                        addEntityAssocField(Referenced.ToOne, entityName, referencedEntityName, field, jaField);

                    } else if (value instanceof OneToMany) {
                        addEntityAssocField(Referenced.Collection, referencedEntityName, entityName, field, jaField);
                    }
                }
            }
        }
    }

    /**
     * 添加分析实体属性
     *
     * @param referenced
     * @param entityName
     * @param referencedEntityName
     * @param field
     * @param jaField
     */
    protected static void addEntityAssocField(Referenced referenced, String entityName, String referencedEntityName, Field field, JaField jaField) {
        addEntityAssocField(referenced, entityName, referencedEntityName, null, field, jaField);
    }

    protected static void addEntityAssocField(Referenced referenced, String entityName, String referencedEntityName, Class<?> referencedEntityClass, Field field, JaField jaField) {
        List<AssocField> assocFields = SessionFactoryUtils.get().getNameMapAssocFields().get(entityName);
        if (assocFields == null) {
            assocFields = new ArrayList<EntityAssoc.AssocField>();
            SessionFactoryUtils.get().getNameMapAssocFields().put(entityName, assocFields);
        }

        if (referencedEntityClass == null) {
            assocFields.add(new AssocField(field.getName(), jaField.assocClasses(), referenced, referencedEntityName));

        } else {
            assocFields.add(new AssocFieldEntity(field.getName(), jaField.assocClasses(), referenced, referencedEntityName, referencedEntityClass));
        }
    }

    public static void boost(SessionFactoryBean sessionFactoryBean) {
        Set<Entry<?, ?>> entries = new HashSet<Entry<?, ?>>();
        entries.addAll(sessionFactoryBean.getNameMapPermissions().entrySet());
        sessionFactoryBean.getNameMapPermissions().clear();
        for (Entry<?, ?> entry : entries) {
            sessionFactoryBean.getNameMapPermissions().put(SessionFactoryUtils.getJpaEntityName((String) entry.getKey()), (JePermission[]) entry.getValue());
        }

        entries.clear();
        entries.addAll(sessionFactoryBean.getNameMapAssocEntities().entrySet());
        sessionFactoryBean.getNameMapAssocEntities().clear();
        for (Entry<?, ?> entry : entries) {
            List<AssocEntity> assocEntities = (List<AssocEntity>) entry.getValue();
            for (AssocEntity assocEntity : assocEntities) {
                assocEntity.entityName = SessionFactoryUtils.getJpaEntityName(assocEntity.entityName);
                assocEntity.referenceEntityName = SessionFactoryUtils.getJpaEntityName(assocEntity.referenceEntityName);
            }

            sessionFactoryBean.getNameMapAssocEntities().put(SessionFactoryUtils.getJpaEntityName((String) entry.getKey()), assocEntities);
        }

        entries.clear();
        entries.addAll(sessionFactoryBean.getNameMapAssocFields().entrySet());
        sessionFactoryBean.getNameMapAssocFields().clear();
        for (Entry<?, ?> entry : entries) {
            List<AssocField> assocFields = (List<AssocField>) entry.getValue();
            List<AssocField> assocFieldList = new ArrayList<EntityAssoc.AssocField>();
            for (AssocField assocField : assocFields) {
                if (assocField instanceof AssocFieldEntity) {
                    if (sessionFactoryBean.getEntityNameMapJpaEntityName().containsKey(assocField.getReferenceEntityName())) {
                        assocField = new AssocField(assocField.getFieldName(), assocField.getAssocClasses(), assocField.getReferenced(), assocField.getReferenceEntityName());

                    } else {
                        assocFieldList.add(new AssocFieldFactory(assocField.getFieldName(), assocField.getAssocClasses(), Referenced.Factory, assocField.getReferenceEntityName(),
                                ((AssocFieldEntity) assocField).getReferenceEntityClass()));
                        continue;
                    }
                }

                assocField.referenceEntityName = SessionFactoryUtils.getJpaEntityName(assocField.referenceEntityName);
                assocFieldList.add(assocField);
            }

            sessionFactoryBean.getNameMapAssocFields().put(SessionFactoryUtils.getJpaEntityName((String) entry.getKey()), assocFieldList);
        }

        entries.clear();
        entries.addAll(sessionFactoryBean.getNameMapEntityAssocEntity().entrySet());
        sessionFactoryBean.getNameMapEntityAssocEntity().clear();
        for (Entry<?, ?> entry : entries) {
            EntityAssocEntity entityAssocEntity = (EntityAssocEntity) entry.getValue();
            entityAssocEntity.assocName = SessionFactoryUtils.getJpaEntityName(entityAssocEntity.assocName);
            sessionFactoryBean.getNameMapEntityAssocEntity().put(SessionFactoryUtils.getJpaEntityName((String) entry.getKey()), entityAssocEntity);
        }
    }

    /**
     * 关联属性类型
     *
     * @author absir
     */
    public static enum Referenced {
        /**
         * 对单关联
         */
        ToOne,
        /**
         * 对多关联
         */
        ToMany,
        /**
         * 集合关联
         */
        Collection,
        /**
         * 关联字段
         */
        Referene,
        /**
         * 集合字段集合
         */
        Referenes,
        /**
         * 工厂模式
         */
        Factory,
    }

    /**
     * 关联实体结构
     *
     * @author absir
     */
    public static class AssocEntity {

        /**
         * 关联实体本身名称 entityName
         */
        private String entityName;

        /**
         * 关联实体本身类型 entityClass
         */
        private Class<? extends JiAssoc> entityClass;

        /**
         * 关联实体支持关联类型 assocClasses
         */
        private Class<?>[] assocClasses;

        /**
         * 关联实体关联至实体名称 referenceEntityName
         */
        private String referenceEntityName;

        /**
         * 关联实体关联至实体类型 referenceEntityClass
         */
        private Class<?> referenceEntityClass;

        private AssocEntity(String assocName, JaAssoc jaAssoc) {
            this.entityName = assocName;
            this.entityClass = (Class<? extends JiAssoc>) jaAssoc.entityClass();
            this.assocClasses = jaAssoc.assocClasses();
            this.referenceEntityName = jaAssoc.referenceEntityName();
            this.referenceEntityClass = jaAssoc.referenceEntityClass();
        }

        /**
         * 关联实体本身名称
         *
         * @return the entityName
         */
        public String getEntityName() {
            return entityName;
        }

        /**
         * 关联实体本身类型
         *
         * @return the entityClass
         */
        public Class<? extends JiAssoc> getEntityClass() {
            return entityClass;
        }

        /**
         * 关联实体支持关联类型
         *
         * @return the assocClasses
         */
        public Class<?>[] getAssocClasses() {
            return assocClasses;
        }

        /**
         * 关联实体关联至实体名称
         *
         * @return the referenceEntityName
         */
        public String getReferenceEntityName() {
            if (KernelString.isEmpty(referenceEntityName)) {
                referenceEntityName = SessionFactoryUtils.getJpaEntityName(referenceEntityClass);
            }

            return referenceEntityName;
        }

        /**
         * 关联实体关联至实体名称
         *
         * @return the referenceEntityClass
         */
        public Class<?> getReferenceEntityClass() {
            return referenceEntityClass;
        }
    }

    /**
     * 关联属性结构
     *
     * @author absir
     */
    public static class AssocField {

        /**
         * 关联属性名称 feildName
         */
        private String feildName;

        /**
         * 关联属性支持关联类型 assocClasses
         */
        private Class<?>[] assocClasses;

        /**
         * 关联属性类型 referenced
         */
        private Referenced referenced;

        /**
         * 关联实体名称 referenceEntityName
         */
        private String referenceEntityName;

        private AssocField(String assocId, Class<?>[] classes, Referenced referenced, String referenceEntityName) {
            this.feildName = assocId;
            this.assocClasses = classes;
            this.referenced = referenced;
            this.referenceEntityName = referenceEntityName;
        }

        /**
         * 关联属性名称
         *
         * @return
         */
        public String getFieldName() {
            return feildName;
        }

        /**
         * 关联属性名称
         *
         * @return
         */
        public Class<?>[] getAssocClasses() {
            return assocClasses;
        }

        /**
         * 关联属性类型
         *
         * @return
         */
        public Referenced getReferenced() {
            return referenced;
        }

        /**
         * 关联实体名称
         *
         * @return
         */
        public String getReferenceEntityName() {
            return referenceEntityName;
        }
    }

    public static class AssocFieldEntity extends AssocField {

        /**
         * 关联实体名称
         */
        private Class<?> referenceEntityClass;

        private AssocFieldEntity(String assocId, Class<?>[] classes, Referenced referenced, String referenceEntityName, Class<?> referenceEntityClass) {
            super(assocId, classes, referenced, referenceEntityName);
            this.referenceEntityClass = referenceEntityClass;
        }

        public Class<?> getReferenceEntityClass() {
            return referenceEntityClass;
        }

        public void setReferenceEntityClass(Class<?> referenceEntityClass) {
            this.referenceEntityClass = referenceEntityClass;
        }
    }

    public static class AssocFieldFactory extends AssocFieldEntity {

        /**
         * 关联实体查询对象
         */
        private Object assocDao;

        private AssocFieldFactory(String assocId, Class<?>[] classes, Referenced referenced, String referenceEntityName, Class<?> referenceEntityClass) {
            super(assocId, classes, referenced, referenceEntityName, referenceEntityClass);
        }

        public Object getAssocDao() {
            return assocDao;
        }

        public void setAssocDao(Object assocDao) {
            this.assocDao = assocDao;
        }
    }

    /**
     * 对映关联结构
     *
     * @author absir
     */
    public static class EntityAssocEntity {

        /**
         * 对映关联实体名 assocName
         */
        private String assocName;

        /**
         * 对映关联实体结构 assocEntity
         */
        private AssocEntity assocEntity;

        private EntityAssocEntity(String assocName, AssocEntity assocEntity) {
            this.assocName = assocName;
            this.assocEntity = assocEntity;
        }

        /**
         * 对映关联实体名
         *
         * @return the assocName
         */
        public String getAssocName() {
            return assocName;
        }

        /**
         * 对映关联实体结构
         *
         * @return the assocEntity
         */
        public AssocEntity getAssocEntity() {
            return assocEntity;
        }
    }

    @SuppressWarnings("serial")
    public static class AssocType extends ManyToOneType {

        public AssocType(TypeScope scope, String referencedEntityName) {
            super(scope, referencedEntityName);
        }

        public AssocType(TypeScope scope, String referencedEntityName, String uniqueKeyPropertyName) {
            super(scope, referencedEntityName, false, uniqueKeyPropertyName, true, false, false, false);
        }
    }
}
