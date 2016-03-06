/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-18 下午3:48:04
 */
package com.absir.aserv.system.bean.type;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class JtJsonDynamic implements UserType, DynamicParameterizedType, Serializable {

	/** dynamicType */
	private Type dynamicType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.usertype.ParameterizedType#setParameterValues(java.util
	 * .Properties)
	 */
	@Override
	public void setParameterValues(Properties parameters) {
		Class<?> entityClass = KernelClass.forName(parameters.getProperty(ENTITY));
		if (entityClass != null) {
			Field field = KernelReflect.declaredField(entityClass, parameters.getProperty(PROPERTY));
			if (field != null) {
				dynamicType = field.getGenericType();
				return;
			}
		}

		dynamicType = KernelClass.forName(parameters.getProperty(RETURNED_CLASS));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		return new int[] { Types.LONGVARCHAR };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class returnedClass() {
		return Object.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
	 * java.lang.String[], org.hibernate.engine.spi.SessionImplementor,
	 * java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException,
			SQLException {
		String value = rs.getString(names[0]);
		if (value == null || value.trim().length() == 0) {
			return null;
		}

		return HelperJson.decodeNull(value, dynamicType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
	 * java.lang.Object, int, org.hibernate.engine.spi.SessionImplementor)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException,
			SQLException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return KernelObject.clone(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
	 * java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
}
