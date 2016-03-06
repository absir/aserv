/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 上午10:34:12
 */
package com.absir.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.property.value.Allow;

/**
 * @author absir
 * 
 */
public class Property {

	/** accessor */
	private Accessor accessor;

	/** type */
	private Class<?> type;

	/** genericType */
	private Type genericType;

	/** hidden <= -2 allow -1(set) 0(set|get) 1(get) */
	private int allow;

	/** include */
	private int include;

	/** exclude */
	private int exclude;

	/** beanName */
	private String beanName;

	/** propertyConvert */
	private PropertyConvert propertyConvert;

	/**
	 * @param beanClass
	 * @param name
	 * @param include
	 * @param exclude
	 * @param beanName
	 * @param factoryClass
	 */
	public Property(Class<?> beanClass, String name, int include, int exclude, String beanName, Class<? extends PropertyFactory> factoryClass) {
		accessor = UtilAccessor.getAccessorProperty(beanClass, name);
		Field field = accessor == null ? null : accessor.getField();
		if (field == null || (!Modifier.isPublic(field.getModifiers()) && field.getAnnotation(Allow.class) == null)) {
			if (accessor == null) {
				allow = -2;

			} else {
				if (accessor.getGetter() == null) {
					allow = accessor.getSetter() == null ? -2 : -1;

				} else {
					allow = accessor.getSetter() == null ? 1 : 0;
				}
			}
		}

		if (allow > -2) {
			if (accessor.getSetter() == null) {
				if (accessor.getGetter() != null) {
					type = accessor.getGetter().getReturnType();
				}

			} else {
				type = accessor.getSetter().getParameterTypes()[0];
			}

			if (field != null) {
				if (type == null || type.isAssignableFrom(field.getType())) {
					type = field.getType();
					genericType = field.getGenericType();
					if (genericType instanceof TypeVariable) {
						genericType = KernelClass.type(beanClass, (TypeVariable<?>) genericType);
						if (genericType != null) {
							type = KernelClass.rawClass(genericType);
						}
					}
				}
			}

			this.include = include;
			this.exclude = exclude;
			this.beanName = beanName;
			this.propertyConvert = factoryClass == null ? null : BeanFactoryUtils.getBeanTypeInstance(factoryClass).getPropertyConvert(this);
		}
	}

	/**
	 * @return the allow
	 */
	public int getAllow() {
		return allow;
	}

	/**
	 * @return
	 */
	public boolean isOpened() {
		return allow == 0;
	}

	/**
	 * @return
	 */
	public boolean isHidden() {
		return allow == 2;
	}

	/**
	 * @return
	 */
	public boolean isReadable() {
		return allow == 0 || allow == 1;
	}

	/**
	 * @return
	 */
	public boolean isWriteable() {
		return allow == 0 || allow == -1;
	}

	/**
	 * @return the field
	 */
	public Field getField() {
		return accessor.getField();
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @return the genericType
	 */
	public Type getGenericType() {
		return genericType;
	}

	/**
	 * @return the accessor
	 */
	public Accessor getAccessor() {
		return accessor;
	}

	/**
	 * @return
	 */
	public Type getAccessorType() {
		return genericType == null ? type : genericType;
	}

	/**
	 * @return the include
	 */
	public int getInclude() {
		return include;
	}

	/**
	 * @return the exclude
	 */
	public int getExclude() {
		return exclude;
	}

	/**
	 * @param group
	 * @return
	 */
	public boolean allow(int group) {
		return group == 0 || ((exclude & group) == 0 && (include == 0 || (include & group) != 0));
	}

	/**
	 * @return the toName
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * @param propertyValue
	 * @return
	 */
	public Object getValue(Object propertyValue) {
		return propertyValue == null || propertyConvert == null ? propertyValue : propertyConvert.getValue(propertyValue);
	}

	/**
	 * @param value
	 * @param beanName
	 * @return
	 */
	public Object getPropertyValue(Object value, String beanName) {
		return value == null || propertyConvert == null ? value : propertyConvert.getPropertyValue(value, beanName);
	}
}
