/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-22 上午9:49:44
 */
package com.absir.orm.hibernate;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocEntity;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocField;
import com.absir.orm.hibernate.boost.EntityAssoc.EntityAssocEntity;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
@Inject
public abstract class SessionFactoryUtils {

    private static final Map<String, Map<String, Object[]>> Entity_Name_Map_Field_Metas = new HashMap<String, Map<String, Object[]>>();

    private static SessionFactoryBean sessionFactoryBean = BeanFactoryUtils.get(SessionFactoryBean.class);

    public static SessionFactoryBean get() {
        return sessionFactoryBean;
    }

    /**
     * 获取实体是否授权
     *
     * @param entityName
     * @param permission
     * @return
     */
    public static boolean entityPermission(String entityName, JePermission permission) {
        JePermission[] jePermissions = sessionFactoryBean.getNameMapPermissions().get(entityName);
        return jePermissions == null ? false : KernelArray.contain(jePermissions, permission);
    }

    /**
     * 获取实体是否授权
     *
     * @param entityName
     * @param permissions
     * @return
     */
    public static boolean entityPermissions(String entityName, JePermission... permissions) {
        JePermission[] jePermissions = sessionFactoryBean.getNameMapPermissions().get(entityName);
        return jePermissions == null ? false : KernelArray.contains(jePermissions, permissions);
    }

    /**
     * 添加实体授权
     *
     * @param name
     * @param permissions
     */
    public static void setEntityPermissions(String name, JePermission[] permissions) {
        if (permissions != null && permissions.length > 0) {
            sessionFactoryBean.getNameMapPermissions().put(name, permissions);
        }
    }

    /**
     * 获取实体关联实体列表
     *
     * @param entityName
     * @return
     */
    public static List<AssocEntity> getEntityAssocEntities(String entityName) {
        return sessionFactoryBean.getNameMapAssocEntities().get(entityName);
    }

    /**
     * 获取关联实体属性列表
     *
     * @param entityName
     * @return
     */
    public static List<AssocField> getEntityAssocFields(String entityName) {
        return sessionFactoryBean.getNameMapAssocFields().get(entityName);
    }

    /**
     * 获取实体名对映关联结构
     *
     * @param entityName
     * @return
     */
    public static EntityAssocEntity getEntityAssocEntity(String entityName) {
        return sessionFactoryBean.getNameMapEntityAssocEntity().get(entityName);
    }

    /**
     * 获取实体简单名称
     *
     * @param entityName
     * @return
     */
    public static String getJpaEntityName(String entityName) {
        if (entityName == null) {
            return null;
        }

        String jpaEntityName = sessionFactoryBean.getEntityNameMapJpaEntityName().get(entityName);
        return jpaEntityName == null ? entityName : jpaEntityName;
    }

    /**
     * 获取实体简单名称
     *
     * @param entityClass
     * @return
     */
    public static String getJpaEntityName(Class<?> entityClass) {
        if (entityClass == null) {
            return null;
        }

        return getJpaEntityName(entityClass.getName());
    }

    /**
     * 获取实体简单名称
     *
     * @param entityClass
     * @return
     */
    public static String getEntityNameNull(Class<?> entityClass) {
        if (entityClass == null) {
            return null;
        }

        return sessionFactoryBean.getEntityNameMapJpaEntityName().get(entityClass.getName());
    }

    /**
     * 获取实体类型
     *
     * @param jpaEntityName
     * @return
     */
    public static Class<?> getEntityClass(String jpaEntityName) {
        if (jpaEntityName == null) {
            return null;
        }

        Entry<Class<?>, SessionFactory> entry = sessionFactoryBean.getJpaEntityNameMapEntityClassFactory()
                .get(jpaEntityName);
        return entry == null ? null : entry.getKey();
    }

    /**
     * 获取实体名称
     *
     * @param jpaEntityName
     * @return
     */
    public static String getEntityName(String jpaEntityName) {
        if (jpaEntityName == null) {
            return null;
        }

        Class<?> entityClass = getEntityClass(jpaEntityName);
        return entityClass == null ? jpaEntityName : entityClass.getName();
    }

    /**
     * 获取实体名称
     *
     * @param entityClass
     * @return
     */
    public static String getEntityName(Class<?> entityClass) {
        if (entityClass == null) {
            return null;
        }

        String entityName = entityClass.getName();
        if (sessionFactoryBean.getEntityNameMapJpaEntityName().get(entityName) == null) {
            return null;
        }

        return entityName;
    }

    /**
     * 获取实体所在工厂
     *
     * @param entityClass
     * @return
     */
    public static SessionFactory getSessionFactory(Class<?> entityClass) {
        String jpaEntityName = getJpaEntityName(entityClass);
        return jpaEntityName == null ? null : getSessionFactory(jpaEntityName);
    }

    /**
     * 获取实体所在工厂
     *
     * @param jpaEntityName
     * @return
     */
    public static SessionFactory getSessionFactory(String jpaEntityName) {
        Entry<Class<?>, SessionFactory> entry = sessionFactoryBean.getJpaEntityNameMapEntityClassFactory()
                .get(jpaEntityName);
        return entry == null ? null : entry.getValue();
    }

    /**
     * 获取实体所在工厂
     *
     * @param jpaEntityName
     * @param entityClass
     * @return
     */
    public static SessionFactory getSessionFactory(String jpaEntityName, Class<?> entityClass) {
        return jpaEntityName == null ? getSessionFactory(jpaEntityName) : getSessionFactory(entityClass);
    }

    /**
     * 获取实体主键名称
     *
     * @param jpaEntityName
     * @param entityClass
     * @return
     */
    public static String getIdentifierName(String jpaEntityName, Class<?> entityClass) {
        return getIdentifierName(jpaEntityName, entityClass, null);
    }

    /**
     * 获取实体主键名称
     *
     * @param jpaEntityName
     * @param entityClass
     * @param sessionFactory
     * @return
     */
    public static String getIdentifierName(String jpaEntityName, Class<?> entityClass, SessionFactory sessionFactory) {
        ClassMetadata classMetadata = getClassMetadata(jpaEntityName, entityClass, sessionFactory);
        return classMetadata == null ? null : classMetadata.getIdentifierPropertyName();
    }

    /**
     * 获取实体主键类型
     *
     * @param jpaEntityName
     * @param entityClass
     * @return
     */
    public static Class<? extends Serializable> getIdentifierType(String jpaEntityName, Class<?> entityClass) {
        return getIdentifierType(jpaEntityName, entityClass, null);
    }

    /**
     * 获取实体主键类型
     *
     * @param jpaEntityName
     * @param entityClass
     * @param sessionFactory
     * @return
     */
    public static Class<? extends Serializable> getIdentifierType(String jpaEntityName, Class<?> entityClass,
                                                                  SessionFactory sessionFactory) {
        ClassMetadata classMetadata = getClassMetadata(jpaEntityName, entityClass, sessionFactory);
        return classMetadata == null ? null : classMetadata.getIdentifierType().getReturnedClass();
    }

    /**
     * 获取实体详细信息
     *
     * @param jpaEntityName
     * @param entityClass
     * @param sessionFactory
     * @return
     */
    public static ClassMetadata getClassMetadata(String jpaEntityName, Class<?> entityClass,
                                                 SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            if (jpaEntityName == null) {
                jpaEntityName = getJpaEntityName(entityClass);
            }

            if (jpaEntityName == null) {
                return null;
            }

            sessionFactory = getSessionFactory(jpaEntityName);
            if (sessionFactory == null) {
                return null;
            }
        }

        if (entityClass == null) {
            return sessionFactory.getClassMetadata(jpaEntityName);
        }

        return sessionFactory.getClassMetadata(entityClass);
    }

    public static Serializable getIdentifierValue(String jpaEntityName, Object entity, Session session,
                                                  SessionFactory sessionFactory) {
        if (sessionFactory == null && session != null) {
            sessionFactory = session.getSessionFactory();
        }

        return getIdentifierValue(getClassMetadata(jpaEntityName, entity.getClass(), sessionFactory), entity, session);
    }

    public static Serializable getIdentifierValue(ClassMetadata classMetadata, Object entity, Session session) {
        return classMetadata == null ? null : classMetadata.getIdentifier(entity, (SessionImplementor) session);
    }

    public static String getReferencedEntityName(String entityName, Field field) {
        return getReferencedEntityName(new JoEntity(entityName, null), field);
    }

    public static String getReferencedEntityName(Class<?> entityClass, Field field) {
        return getReferencedEntityName(new JoEntity(null, entityClass), field);
    }

    public static String getReferencedEntityName(JoEntity joEntity, Field field) {
        if (joEntity.getEntityClass() == null) {
            return null;
        }

        String referencedEntityName = null;
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if (oneToOne != null && oneToOne.targetEntity() != void.class) {
            return oneToOne.targetEntity().getName();
        }

        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null && manyToOne.targetEntity() != void.class) {
            return manyToOne.targetEntity().getName();
        }

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null && oneToMany.targetEntity() != void.class) {
            return oneToMany.targetEntity().getName();
        }

        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (manyToMany != null && manyToMany.targetEntity() != void.class) {
            return manyToMany.targetEntity().getName();
        }

        referencedEntityName = getEntityName(KernelClass.componentClass(field.getGenericType()));
        return referencedEntityName;
    }

    public static String[] getReferencedEntityNames(String entityName, Field field) {
        return getReferencedEntityNames(new JoEntity(entityName, null), field);
    }

    public static String[] getReferencedEntityNames(JoEntity joEntity, Field field) {
        if (joEntity.getEntityClass() == null) {
            return null;
        }

        String[] referencedEntityNames = new String[2];
        if (Map.class.isAssignableFrom(field.getType())) {
            Type[] typeArgs = KernelClass.typeArguments(field.getGenericType());
            if (typeArgs != null && typeArgs.length > 0) {
                if (KernelString.isEmpty(referencedEntityNames[0])) {
                    referencedEntityNames[1] = getEntityName(KernelClass.rawClass(typeArgs[0]));
                }

                if (KernelString.isEmpty(referencedEntityNames[1]) && typeArgs.length > 1) {
                    referencedEntityNames[0] = getReferencedEntityName(joEntity, field);
                    if (referencedEntityNames[0] == null) {
                        referencedEntityNames[0] = getEntityName(KernelClass.rawClass(typeArgs[1]));
                    }
                }
            }

        } else if (KernelString.isEmpty(referencedEntityNames[0])) {
            referencedEntityNames[0] = getReferencedEntityName(joEntity, field);
        }

        return referencedEntityNames;
    }

    public static String[] getEntityNames(String[] jpaEntityNames) {
        if (jpaEntityNames == null) {
            return null;
        }

        int length = jpaEntityNames.length;
        for (int i = 0; i < length; i++) {
            jpaEntityNames[i] = getEntityName(jpaEntityNames[i]);
        }

        return jpaEntityNames;
    }

    public static String[] getJpaEntityNames(String[] entityNames) {
        if (entityNames == null) {
            return null;
        }

        int length = entityNames.length;
        for (int i = 0; i < length; i++) {
            entityNames[i] = getJpaEntityName(entityNames[i]);
        }

        return entityNames;
    }

    public static String getReferencedJpaEntityName(JoEntity joEntity, Field field) {
        return getJpaEntityName(getReferencedEntityName(joEntity, field));
    }

    public static String[] getReferencedJpaEntityNames(JoEntity joEntity, Field field) {
        return getJpaEntityNames(getReferencedEntityNames(joEntity, field));
    }

    public static Map<String, Object[]> getEntityFieldMetas(final String jpaEntityName, Class<?> entityClass) {
        if (entityClass == null) {
            entityClass = getEntityClass(jpaEntityName);
        }

        Map<String, Object[]> metas = Entity_Name_Map_Field_Metas.get(jpaEntityName);
        if (metas == null) {
            synchronized (entityClass) {
                metas = Entity_Name_Map_Field_Metas.get(jpaEntityName);
                if (metas == null) {
                    final Map<String, Object[]> metaDefines = new HashMap<String, Object[]>();
                    KernelReflect.doWithDeclaredFields(entityClass, new CallbackBreak<Field>() {

                        @Override
                        public void doWith(Field template) throws BreakException {
                            if ((PropertyUtils.TRANSIENT_MODIFIER & template.getModifiers()) == 0) {
                                Object[] ms = null;
                                Class<?> type = template.getType();
                                if (KernelClass.isBasicClass(type) || type == Serializable.class) {
                                    ms = new Object[1];
                                    ms[0] = type;

                                } else {
                                    if (Map.class.isAssignableFrom(type)) {
                                        String[] referencedEntityNames = getJpaEntityNames(
                                                getReferencedEntityNames(jpaEntityName, template));
                                        if (referencedEntityNames != null) {
                                            if (referencedEntityNames[0] != null || referencedEntityNames[1] != null) {
                                                ms = new Object[3];
                                                ms[0] = type;
                                                ms[1] = referencedEntityNames[0];
                                                ms[2] = referencedEntityNames[1];
                                            }
                                        }

                                    } else {
                                        String referencedEntityName = getJpaEntityName(
                                                getReferencedEntityName(jpaEntityName, template));
                                        if (referencedEntityName != null) {
                                            ms = new Object[2];
                                            ms[0] = type;
                                            ms[1] = referencedEntityName;
                                        }
                                    }
                                }

                                if (ms != null) {
                                    metaDefines.put(template.getName(), ms);
                                }
                            }
                        }
                    });

                    metas = metaDefines;
                    Entity_Name_Map_Field_Metas.put(jpaEntityName, metas);
                }
            }
        }

        return metas;
    }
}
