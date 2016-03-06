/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-24 下午3:40:07
 */
package com.absir.aop;

import java.util.Collections;
import java.util.List;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.core.BeanDefineWrapper;
import com.absir.bean.core.BeanFactoryParameters;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelLang;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AopBeanDefine extends BeanDefineWrapper {

	/** aopInterceptors */
	private List<AopInterceptor> aopInterceptors;

	/**
	 * @param beanDefine
	 */
	public AopBeanDefine(BeanDefine beanDefine) {
		super(beanDefine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrapper#getBeanProxy(java.lang.Object,
	 * com.absir.bean.basis.BeanDefine, com.absir.bean.basis.BeanFactory)
	 */
	@Override
	public Object getBeanProxy(Object beanObject, BeanDefine beanDefineRoot, BeanFactory beanFactory) {
		if (aopInterceptors == null) {
			aopInterceptors = AopDefineProcessor.getAopInterceptors(beanDefineRoot, beanObject);
			if (aopInterceptors == null || aopInterceptors.isEmpty()) {
				aopInterceptors = KernelLang.NULL_LIST_SET;

			} else {
				aopInterceptors = Collections.unmodifiableList(aopInterceptors);
			}
		}

		if (!aopInterceptors.isEmpty()) {
			beanObject = AopProxyUtils.proxyInterceptors(beanObject, aopInterceptors);
		}

		return beanObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.core.BeanDefineWrappered#retrenchBeanDefine()
	 */
	@Override
	public BeanDefine retrenchBeanDefine() {
		if (beanDefine instanceof BeanDefineWrapper) {
			beanDefine = ((BeanDefineWrapper) beanDefine).retrenchBeanDefine();
		}

		return aopInterceptors.isEmpty() ? beanDefine : this;
	}

	/**
	 * @param beanType
	 * @return
	 */
	public static <T> T instanceBeanObject(Class<?> beanType) {
		return (T) new AopBeanDefine(new BeanDefineType("@", beanType)).getBeanObject(BeanFactoryUtils.get());
	}

	/**
	 * @param beanType
	 * @return
	 */
	public static <T> T instanceBeanObject(Class<?> beanType, Object... initargs) {
		return (T) new AopBeanDefine(new BeanDefineType("@", beanType)).getBeanObject(new BeanFactoryParameters(BeanFactoryUtils.get(), initargs));
	}
}
