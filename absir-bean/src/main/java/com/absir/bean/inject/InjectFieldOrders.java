/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-19 下午2:43:31
 */
package com.absir.bean.inject;

import java.lang.reflect.Field;
import java.util.List;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 * 
 */
public class InjectFieldOrders extends InjectField {

	/** beanType */
	Class<?> beanType;

	/**
	 * @param field
	 * @param injectName
	 * @param injectType
	 * @param orders
	 * @return
	 */
	public static InjectField getInjectField(Field field, String injectName, InjectType injectType, Orders orders) {
		Class<?> beanType = null;
		if (orders != null) {
			if (field.getType().isArray() || List.class.isAssignableFrom(field.getType())) {
				beanType = KernelClass.componentClass(field.getGenericType());
				if (beanType != null && Orderable.class.isAssignableFrom(beanType)) {
					beanType = null;
				}
			}
		}

		return beanType == null ? new InjectField(field, injectName, injectType) : new InjectFieldOrders(field, injectName, injectType, beanType);
	}

	/**
	 * @param field
	 * @param injectName
	 * @param injectType
	 */
	private InjectFieldOrders(Field field, String injectName, InjectType injectType, Class<?> beanType) {
		super(field, injectName, injectType);
		this.beanType = beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.InjectInvoker#parameter(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	protected Object parameter(BeanFactory beanFactory) {
		List<?> beanObjects = beanFactory.getBeanObjects(beanType);
		if (beanObjects == null || beanObjects.isEmpty()) {
			if (injectType == InjectType.Required) {
				throw new RuntimeException("BeanName = " + value + " is " + beanObjects + " not match " + beanType);
			}

			return null;
		}

		KernelList.sortCommonObjects(beanObjects);
		return DynaBinder.INSTANCE.bind(beanObjects, null, field.getGenericType());
	}
}
