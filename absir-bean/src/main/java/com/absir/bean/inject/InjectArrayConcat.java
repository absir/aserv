/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-10 下午4:17:59
 */
package com.absir.bean.inject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectConcat;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelReflect;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class InjectArrayConcat extends InjectInvoker {

	/** field */
	Field field;

	/** componentClass */
	Class<?> componentClass;

	/**
	 * @param field
	 * @param value
	 */
	public InjectArrayConcat(Field field, InjectConcat injectConcat) {
		this.field = field;
		componentClass = field.getType().getComponentType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.inject.InjectInvoker#invoke(com.absir.bean.basis.BeanFactory
	 * , java.lang.Object)
	 */
	@Override
	public void invoke(BeanFactory beanFactory, Object beanObject) {
		ArrayList<Object> values = DynaBinder.to(KernelReflect.get(beanObject, field), ArrayList.class);
		List<?> concats = beanFactory.getBeanObjects(componentClass);
		for (Object concat : concats) {
			if (!values.contains(concat)) {
				values.add(concat);
			}
		}

		KernelReflect.set(beanObject, field, DynaBinder.INSTANCE.bind(values, null, field.getGenericType()));
	}
}
