/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-24 下午3:40:07
 */
package com.absir.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.absir.aop.value.Impl;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanDefineProcessor;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.InjectBeanFactory;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Basis
public class AopDefineProcessor implements IBeanDefineSupply, IBeanDefineProcessor, IBeanFactoryAware {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return -255;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineSupply#getBeanDefines(com.absir.bean
	 * .core.BeanFactoryImpl, java.lang.Class)
	 */
	@Override
	public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
		Impl basic = beanType.getAnnotation(Impl.class);
		if (basic != null || beanType.isInterface() || Modifier.isAbstract(beanType.getModifiers())) {
			Bean bean = beanType.getAnnotation(Bean.class);
			if (basic != null || bean != null) {
				AopImplDefine beanDefine = new AopImplDefine(BeanDefineType.getBeanName(bean == null ? null : bean.value(), beanType), beanType, bean == null ? null : bean.scope(),
						basic == null ? null : basic.value());
				return InjectBeanFactory.getInstance().getBeanDefines(beanFactory, beanType, beanDefine);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineProcessor#getBeanDefine(com.absir.bean
	 * .basis.BeanFactory, com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public BeanDefine getBeanDefine(BeanFactory beanFactory, BeanDefine beanDefine) {
		return new AopBeanDefine(beanDefine);
	}

	/** asynMethodDefines */
	private static AopMethodDefine[] aopMethodDefines = KernelCollection.toArray(BeanFactoryUtils.getOrderBeanObjects(AopMethodDefine.class), AopMethodDefine.class);

	/**
	 * @author absir
	 * 
	 */
	private static class AopInterceptorHolder {

		/** aopInterceptor */
		AopInterceptor aopInterceptor;

		/** variable */
		Object variable;

		/** interceptor */
		Object interceptor;
	}

	/**
	 * @param beanDefine
	 * @param beanObject
	 * @return
	 */
	public static List<AopInterceptor> getAopInterceptors(BeanDefine beanDefine, Object beanObject) {
		final int length = aopMethodDefines.length;
		if (length == 0) {
			return null;
		}

		final List<AopInterceptorHolder> aopInterceptorHolders = new ArrayList<AopInterceptorHolder>(length);
		final Class<?> beanType = AopProxyUtils.getBeanType(beanObject);
		for (int i = 0; i < length; i++) {
			final AopMethodDefine aopMethodDefine = aopMethodDefines[i];
			AopInterceptor aopInterceptor = aopMethodDefine.getAopInterceptor(beanDefine, beanObject);
			AopInterceptorHolder aopInterceptorHolder = null;
			if (aopInterceptor != null) {
				aopInterceptorHolder = new AopInterceptorHolder();
				aopInterceptorHolder.aopInterceptor = aopInterceptor;
				aopInterceptorHolder.variable = aopMethodDefine.getVariable(aopInterceptor, beanDefine, beanObject);
				final AopInterceptorHolder holder = aopInterceptorHolder;
				KernelClass.doWithAncestClass(beanType, new CallbackBreak<Class<?>>() {

					@Override
					public void doWith(Class<?> template) throws BreakException {
						holder.interceptor = aopMethodDefine.getAopInterceptor(holder.variable, template);
						if (holder.interceptor != null) {
							throw new BreakException();
						}
					}
				});
			}

			aopInterceptorHolders.add(aopInterceptorHolder);
		}

		final Map<Method, Set<AopMethodDefine>> methodMapMethodDefines = new HashMap<Method, Set<AopMethodDefine>>();
		KernelClass.doWithAncestClass(beanType, new CallbackBreak<Class<?>>() {

			@Override
			public void doWith(Class<?> template) throws BreakException {
				addAopInterceptors(length, aopInterceptorHolders, beanType, template, methodMapMethodDefines);
			}
		});

		Iterator<AopInterceptorHolder> iterator = aopInterceptorHolders.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			AopInterceptorHolder holder = iterator.next();
			if (holder == null || aopMethodDefines[i].isEmpty(holder.aopInterceptor)) {
				iterator.remove();
			}

			i++;
		}

		int size = aopInterceptorHolders.size();
		if (size == 0) {
			return null;

		} else {
			List<AopInterceptor> aopInterceptors = new ArrayList<AopInterceptor>(size);
			for (AopInterceptorHolder holder : aopInterceptorHolders) {
				aopInterceptors.add(holder.aopInterceptor);
			}

			return aopInterceptors;
		}
	}

	/**
	 * @param length
	 * @param aopInterceptorHolders
	 * @param beanType
	 * @param beanClass
	 * @param methodMapMethodDefines
	 */
	private static void addAopInterceptors(int length, List<AopInterceptorHolder> aopInterceptorHolders, Class<?> beanType, Class<?> beanClass, Map<Method, Set<AopMethodDefine>> methodMapMethodDefines) {
		List<Method> bridgeMethods = null;
		for (Method method : beanClass.getDeclaredMethods()) {
			Method beanMethod = null;
			if (!(Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers()) || Modifier.isFinal(method.getModifiers()))) {
				for (int i = 0; i < length; i++) {
					AopInterceptorHolder holder = aopInterceptorHolders.get(i);
					if (holder != null) {
						AopMethodDefine aopMethodDefine = aopMethodDefines[i];
						Object interceptor = aopMethodDefine.getAopInterceptor(holder.interceptor, holder.variable, beanType, method);
						if (interceptor != null) {
							if (beanMethod == null) {
								beanMethod = InjectBeanFactory.getInstance().getBeanMethod(beanType, method);
								if (beanMethod == null) {
									break;
								}
							}

							Set<AopMethodDefine> aopMethodDefines = methodMapMethodDefines.get(beanMethod);
							if (aopMethodDefines == null) {
								methodMapMethodDefines.put(beanMethod, new HashSet<AopMethodDefine>());

							} else if (!aopMethodDefines.add(aopMethodDefine)) {
								continue;
							}

							aopMethodDefine.setAopInterceptor(interceptor, holder.aopInterceptor, beanType, method, beanMethod);
							if (bridgeMethods == null) {
								bridgeMethods = new ArrayList<Method>();
								for (Method bridgeMethod : beanType.getDeclaredMethods()) {
									if (bridgeMethod.isBridge()) {
										bridgeMethods.add(bridgeMethod);
									}
								}
							}

							Class<?>[] parameterTypes = beanMethod.getParameterTypes();
							for (Method bridgeMethod : bridgeMethods) {
								if (bridgeMethod.getName().equals(beanMethod.getName()) && KernelClass.isAssignableFrom(bridgeMethod.getParameterTypes(), parameterTypes)) {
									aopMethodDefine.setAopInterceptor(interceptor, holder.aopInterceptor, beanType, method, bridgeMethod);
								}
							}
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryAware#beforeRegister(com.absir.bean
	 * .core.BeanFactoryImpl)
	 */
	@Override
	public void beforeRegister(BeanFactoryImpl beanFactory) {
		if (aopMethodDefines == null) {
			aopMethodDefines = KernelCollection.toArray(BeanFactoryUtils.getOrderBeanObjects(AopMethodDefine.class), AopMethodDefine.class);

		} else {
			KernelArray.concat(aopMethodDefines, KernelCollection.toArray(BeanFactoryUtils.getOrderBeanObjects(AopMethodDefine.class), AopMethodDefine.class));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryAware#afterRegister(com.absir.bean.
	 * core.BeanFactoryImpl)
	 */
	@Override
	public void afterRegister(BeanFactoryImpl beanFactory) {
	}
}
