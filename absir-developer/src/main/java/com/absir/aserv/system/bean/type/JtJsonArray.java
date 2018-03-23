/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 下午3:48:04
 */
package com.absir.aserv.system.bean.type;

import com.absir.aserv.system.bean.value.JaStrict;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

@SuppressWarnings({"rawtypes", "serial"})
public class JtJsonArray implements UserType, DynamicParameterizedType, Serializable {

    private Class<?> dynamicType;

    private boolean strict;

    @Override
    public void setParameterValues(Properties parameters) {
        Class<?> entityClass = KernelClass.forName(parameters.getProperty(ENTITY));
        if (entityClass != null) {
            Field field = KernelReflect.declaredField(entityClass, parameters.getProperty(PROPERTY));
            if (field != null) {
                dynamicType = field.getType();
                strict = field.getAnnotation(JaStrict.class) != null;
            }
        }

        if (dynamicType == null) {
            dynamicType = KernelClass.forName(parameters.getProperty(RETURNED_CLASS));
        }

        if (!dynamicType.isArray() || !KernelClass.isBasicClass(dynamicType.getComponentType())) {
            throw new HibernateException("JtJsonArray dynamicType must be basic array");
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return Object.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException,
            SQLException {
        String value = rs.getString(names[0]);
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        try {
            return KernelArray.deserialize(null, value, dynamicType.getComponentType());

        } catch (Throwable e) {
            if (strict) {
                throw new HibernateException(e);
            }

            Environment.throwable(e);
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException,
            SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);

        } else {
            try {
                st.setString(index, KernelArray.serializer(null, value));

            } catch (Throwable e) {
                if (strict) {
                    throw new HibernateException(e);
                }

                Environment.throwable(e);
                st.setString(index, "");
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return KernelObject.clone(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
