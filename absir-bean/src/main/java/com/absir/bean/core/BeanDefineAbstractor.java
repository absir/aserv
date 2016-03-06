/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-20 下午1:39:32
 */
package com.absir.bean.core;

import java.util.ArrayList;
import java.util.List;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
public abstract class BeanDefineAbstractor extends BeanDefineAbstract {

	/** loaded */
	private boolean loaded;

	/**
	 * 
	 */
	public abstract void preloadBeanDefine();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
	 * BeanFactory, com.absir.bean.basis.BeanDefine,
	 * com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
		if (loaded) {
			BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
			if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
				return beanDefineLoaded.getBeanObject(beanFactory);
			}

		} else {
			if (beanDefineRoot.getBeanScope() != BeanScope.PROTOTYPE) {
				loaded = true;
				preloadBeanDefine();
				BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
				if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
					return beanDefineLoaded.getBeanObject(beanFactory);
				}
			}
		}

		return getBeanObject(beanFactory, this, beanDefineRoot, beanDefineWrapper);
	}

	/**
	 * @param type
	 */
	public static void loadInterfaces(Class<?> type) {
		while (type != null && type != Object.class) {
			for (Class<?> iCls : type.getInterfaces()) {
				KernelClass.forName(iCls.getName());
			}

			type = type.getSuperclass();
		}
	}

	/** proccessDelay */
	public static boolean proccessDelay;

	/** proccessRunables */
	public static List<Runnable> proccessRunables;

	/**
	 * 开启延时处理对象
	 * 
	 * @return
	 */
	public static boolean openProccessDelay() {
		if (!proccessDelay) {
			synchronized (BeanDefineAbstractor.class) {
				proccessDelay = true;
				if (proccessRunables == null) {
					proccessRunables = new ArrayList<Runnable>();
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * 移除延时处理对象
	 */
	public static void removeProccessDelay() {
		proccessDelay = false;
	}

	/**
	 * 清算延时处理对象
	 */
	public static void clearProccessDelay() {
		if (proccessRunables != null) {
			synchronized (BeanDefineAbstractor.class) {
				if (proccessRunables != null) {
					List<Runnable> runnables = proccessRunables;
					proccessDelay = false;
					proccessRunables = null;
					for (Runnable runnable : runnables) {
						runnable.run();
					}
				}
			}
		}
	}

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @param beanDefineRoot
	 * @param beanDefineWrapper
	 * @return
	 */
	public static Object getBeanObject(final BeanFactory beanFactory, BeanDefine beanDefine, BeanDefine beanDefineRoot,
			BeanDefine beanDefineWrapper) {
		final Object beanObject = beanDefine.getBeanObject(beanFactory);
		if (beanDefine instanceof BeanDefineAbstractor && ((BeanDefineAbstractor) beanDefine).loaded) {
			BeanDefine beanDefineLoaded = beanFactory.getBeanDefine(beanDefineRoot.getBeanName());
			if (beanDefineLoaded != null && beanDefineLoaded != beanDefineRoot) {
				return beanDefineLoaded.getBeanObject(beanFactory);
			}
		}

		final BeanScope beanScope = beanDefineRoot.getBeanScope();
		Object beanProxy = beanObject;
		if (beanDefineRoot != null) {
			beanProxy = beanDefineRoot.getBeanProxy(beanProxy, beanDefineRoot, beanFactory);
		}

		beanDefine = getBeanDefine(beanDefineRoot.getBeanType(), beanDefineRoot.getBeanName(), beanProxy, beanScope,
				beanDefine);
		if (beanDefineWrapper != null && beanDefineWrapper instanceof BeanDefineWrapper) {
			((BeanDefineWrapper) beanDefineWrapper).beanDefine = beanDefine;
		}

		if (beanScope == BeanScope.PROTOTYPE) {
			beanDefineWrapper = beanDefine;
			if (beanDefineRoot != null && beanDefineRoot instanceof BeanDefineWrapper) {
				beanDefine = ((BeanDefineWrapper) beanDefineRoot).retrenchBeanDefine();
			}
		}

		if (beanDefine != beanDefineWrapper) {
			BeanDefine registeredBeanDefine = beanFactory.getBeanDefine(beanDefineWrapper.getBeanName());
			if (registeredBeanDefine != null
					&& BeanFactoryImpl.containBeanDefine(registeredBeanDefine, beanDefineWrapper)) {
				BeanFactoryImpl.getBeanFactoryImpl(beanFactory).replaceRegisteredBeanDefine(beanDefine);
			}
		}

		if (proccessDelay) {
			// 延迟处理对象
			synchronized (BeanDefineAbstractor.class) {
				if (proccessDelay) {
					final BeanDefine root = beanDefineRoot;
					final Object proxy = beanProxy;
					proccessRunables.add(new Runnable() {

						@Override
						public void run() {
							beanFactory.processBeanObject(beanScope, root, beanObject, proxy);
						}
					});

					return beanProxy;
				}
			}

		} else {
			clearProccessDelay();
		}

		beanFactory.processBeanObject(beanScope, beanDefineRoot, beanObject, beanProxy);
		return beanProxy;
	}
}
