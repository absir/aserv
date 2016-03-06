/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-27 下午5:37:41
 */
package com.absir.bean.core;

import java.lang.reflect.Modifier;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

/**
 * @author absir
 * 
 */
public class BeanDefineOriginal extends BeanDefineWrappered {

	/** beanComponent */
	private Object beanComponent;

	/** beanObject */
	private Object beanObject;

	/**
	 * @param beanDefine
	 */
	public BeanDefineOriginal(BeanDefine beanDefine) {
		super(beanDefine);
		beanComponent = beanDefine.getBeanComponent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return beanComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.bean.value.IBeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return BeanScope.SINGLETON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanObject(com.absir.bean
	 * .basis.BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		if (beanObject == null) {
			Object bean = beanDefine.getBeanObject(beanFactory);
			if (beanObject == null) {
				beanObject = bean;
				beanDefine = BeanDefineAbstract.getBeanDefine(getBeanType(), getBeanName(), beanObject, getBeanScope(), beanDefine);
			}
		}

		return beanObject;
	}

	/**
	 * @param beanType
	 * @return
	 */
	public static boolean isAbstractBeanType(Class<?> beanType) {
		return beanType.isInterface() || Modifier.isAbstract(beanType.getModifiers());
	}
}