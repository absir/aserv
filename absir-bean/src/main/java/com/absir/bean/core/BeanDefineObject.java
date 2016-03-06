/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年1月27日 下午12:10:45
 */
package com.absir.bean.core;

import java.lang.reflect.Method;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.config.IBeanObject;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelReflect;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
public class BeanDefineObject extends BeanDefineAbstractor {

	/** beanMethod */
	private Method beanMethod;

	/** beanType */
	private Class<?> beanType;

	/** beanDefine */
	private BeanDefine beanDefine;

	/**
	 * @param method
	 * @param beanDefine
	 */
	public BeanDefineObject(Method method, BeanDefine beanDefine) {
		beanMethod = method;
		beanType = method.getReturnType();
		this.beanDefine = beanDefine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanType()
	 */
	@Override
	public Class<?> getBeanType() {
		return beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return beanDefine.getBeanScope();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return beanMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineAbstractor#preloadBeanDefine()
	 */
	@Override
	public void preloadBeanDefine() {
		KernelClass.forName(beanMethod.getDeclaringClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		IBeanObject factoryBean = (IBeanObject) beanDefine.getBeanObject(beanFactory);
		return factoryBean == null ? null : factoryBean.getBeanObject();
	}

	/**
	 * @param beanType
	 * @param beanDefine
	 * @return
	 */
	public static BeanDefine getBeanDefine(Class<?> beanType, BeanDefine beanDefine) {
		if (BeanFactory.class.isAssignableFrom(beanType)) {
			Method method = KernelReflect.method(beanType, "getBeanObject");
			if (method != null) {
				return new BeanDefineObject(method, beanDefine);
			}
		}

		return beanDefine;
	}
}
