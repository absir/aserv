/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-13 下午8:30:53
 */
package com.absir.context.config;

import java.lang.reflect.Type;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstract;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;

/**
 * @author absir
 * 
 */
public class BeanDefineReference extends BeanDefineAbstract {

	/** required */
	private boolean required;

	/** beanName */
	private String beanName;

	/** propertyPath */
	private String propertyPath;

	/**
	 * @param name
	 */
	public BeanDefineReference(String name, String required) {
		if (name != null) {
			String[] names = name.split("\\.", 2);
			beanName = names[0];
			if (names.length > 1) {
				propertyPath = names[1];
			}
		}

		this.required = KernelDyna.to(required, boolean.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanType()
	 */
	@Override
	public Class<?> getBeanType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		return getBeanReference(beanFactory, beanName, null);
	}

	/**
	 * @return
	 */
	public Object getBeanReference(BeanFactory beanFactory, String paramName, Type parameterType) {
		Object beanObject = beanFactory.getBeanObject(KernelString.isEmpty(beanName) ? paramName : beanName, parameterType, required);
		if (!KernelString.isEmpty(propertyPath)) {
			beanObject = UtilAccessor.getAccessorObj(beanObject, propertyPath);
		}

		return beanObject;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
}
