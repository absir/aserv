/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-10 上午11:56:50
 */
package com.absir.orm.value;

import com.absir.aop.AopProxy;
import com.absir.core.kernel.KernelObject;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.SessionFactoryUtils;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class JoEntity implements Serializable {

    private static final Map<JoEntity, Object> Jo_Entity_Map_Token = new HashMap<JoEntity, Object>();

    private String entityName;

    private Class<?> entityClass;

    public JoEntity() {
    }

    public JoEntity(String entityName, Class<?> entityClass) {
        if (entityName == null) {
            entityClass = entityClass(entityClass);
            entityName = SessionFactoryUtils.getEntityNameNull(entityClass);

        } else if (entityClass == null) {
            entityName = SessionFactoryUtils.getJpaEntityName(entityName);
            entityClass = SessionFactoryUtils.getEntityClass(entityName);
        }

        this.entityName = entityName;
        this.entityClass = entityClass;
    }

    public static Class<?> entityClass(Class<?> entityClass) {
        while (AopProxy.class.isAssignableFrom(entityClass) || HibernateProxy.class.isAssignableFrom(entityClass)) {
            entityClass = entityClass.getSuperclass();
        }

        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    protected void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    protected void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Object getEntityToken() {
        return entityClass == null ? UtilAbsir.getToken(entityName, Jo_Entity_Map_Token) : entityClass;
    }

    @Override
    public String toString() {
        return entityName + ":" + entityClass;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(entityName) + KernelObject.hashCode(entityClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj != null && obj instanceof JoEntity) {
            JoEntity joEntity = (JoEntity) obj;
            return KernelObject.equals(entityName, joEntity.entityName) && KernelObject.equals(entityClass, joEntity.entityClass);
        }

        return false;
    }
}
