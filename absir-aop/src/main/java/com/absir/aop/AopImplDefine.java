/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-13 下午5:25:49
 */
package com.absir.aop;

import java.util.List;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineAbstractor;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class AopImplDefine extends BeanDefineAbstractor {

	/** beanType */
	private Class<?> beanType;

	/** beanScope */
	private BeanScope beanScope;

	/** implName */
	private String implName;

	/**
	 * @param beanName
	 * @param beanType
	 * @param beanScope
	 * @param implName
	 */
	public AopImplDefine(String beanName, Class<?> beanType, BeanScope beanScope, String implName) {
		this.beanName = beanName;
		this.beanType = beanType;
		this.beanScope = beanScope == null ? BeanScope.SINGLETON : beanScope;
		this.implName = KernelString.isEmpty(implName) ? null : implName;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return implName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanScope()
	 */
	@Override
	public BeanScope getBeanScope() {
		return beanScope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanComponent()
	 */
	@Override
	public Object getBeanComponent() {
		return beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineAbstractor#preloadBeanDefine()
	 */
	@Override
	public void preloadBeanDefine() {
		KernelClass.forName(beanType.getName());
	}

	/**
	 * @param beanDefine
	 * @param cls
	 * @return
	 */
	private boolean circleBeanDefine(BeanDefine beanDefine, Class<?> cls) {
		return beanType == beanDefine.getBeanType()
				|| !(BeanFactoryImpl.getBeanDefine(beanDefine, AopImplDefine.class) == null || beanDefine.getBeanType() == cls);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory) {
		BeanDefine beanDefine = null;
		if (beanType.isInterface()) {
			for (Class<?> cls : beanType.getInterfaces()) {
				if (implName == null) {
					List<BeanDefine> beanDefines = beanFactory.getBeanDefines(cls);
					if (beanDefines.size() > 1) {
						for (BeanDefine define : beanDefines) {
							if (circleBeanDefine(define, cls)) {
								continue;
							}

							beanDefine = define;
							break;
						}
					}

				} else {
					beanDefine = beanFactory.getBeanDefine(implName, cls);
					if (beanDefine != null && circleBeanDefine(beanDefine, cls)) {
						beanDefine = null;
					}
				}

				if (beanDefine != null) {
					break;
				}
			}
		}

		return AopProxyUtils.proxyInterceptors(beanDefine == null ? null : beanDefine.getBeanObject(beanFactory), beanType, null);
	}
}
