/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 下午3:48:04
 */
package com.absir.aserv.system.bean.type;

import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelObject;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class JtJsonValue implements UserType, Serializable {

    private Class valueType;

    public JtJsonValue() {
    }

    public JtJsonValue(String classname) throws ClassNotFoundException {
        this();
        this.valueType = Class.forName(classname);
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.LONGVARCHAR};
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

        if (x == null || y == null) {
            return false;
        }

        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return HelperJson.decodeNull(value, valueType);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            try {
                st.setString(index, HelperJson.encode(value));

            } catch (IOException e) {
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
