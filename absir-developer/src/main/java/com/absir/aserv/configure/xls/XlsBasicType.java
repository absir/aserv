/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-14 下午12:47:31
 */
package com.absir.aserv.configure.xls;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import com.absir.bean.basis.Basis;
import com.absir.orm.value.JiType;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
@Basis
public class XlsBasicType extends AbstractSingleColumnStandardBasicType<XlsBase> implements DiscriminatorType<XlsBase>, JiType {

	/** Xls_Base_Map_Type */
	private static Map<Class<? extends XlsBase>, XlsBasicType> Xls_Base_Map_Type = new HashMap<Class<? extends XlsBase>, XlsBasicType>();

	/**
	 * 
	 */
	public XlsBasicType() {
		this(XlsBase.class);
	}

	/**
	 * @param type
	 */
	public XlsBasicType(Class<? extends XlsBase> type) {
		super(VarcharTypeDescriptor.INSTANCE, new XlsBaseTypeDescriptor(type));
		Xls_Base_Map_Type.put(type, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.type.Type#getName()
	 */
	@Override
	public String getName() {
		return XlsBase.class.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.type.AbstractStandardBasicType#registerUnderJavaType()
	 */
	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

	/**
	 * @param value
	 * @return
	 */
	@Override
	public String toString(XlsBase value) {
		return getJavaTypeDescriptor().toString(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.type.LiteralType#objectToSQLString(java.lang.Object,
	 * org.hibernate.dialect.Dialect)
	 */
	@Override
	public String objectToSQLString(XlsBase value, Dialect dialect) throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.type.IdentifierType#stringToObject(java.lang.String)
	 */
	@Override
	public XlsBase stringToObject(String string) throws Exception {
		return getJavaTypeDescriptor().fromString(string);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.support.entity.value.JiType#byClass(java.lang.Class,
	 * java.util.Properties)
	 */
	@Override
	public Type byClass(Class<?> typeClass, Properties parameters) {
		if (XlsBase.class.isAssignableFrom(typeClass)) {
			XlsBasicType type = Xls_Base_Map_Type.get(typeClass);
			if (type == null) {
				synchronized (typeClass) {
					type = Xls_Base_Map_Type.get(typeClass);
					if (type == null) {
						return new XlsBasicType((Class<? extends XlsBase>) typeClass);
					}
				}
			}

			return type;
		}

		return null;
	}
}
