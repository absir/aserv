/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-6-17 下午4:25:30
 */
package com.absir.bean.inject;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.basis.Configure;
import com.absir.bean.config.IBeanDefineAware;
import com.absir.bean.config.IBeanDefineProcessor;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.config.IBeanFactoryStarted;
import com.absir.bean.config.IBeanFactoryStopping;
import com.absir.bean.config.IBeanFactorySupport;
import com.absir.bean.config.IBeanObjectProcessor;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanDefineMethod;
import com.absir.bean.core.BeanDefineObject;
import com.absir.bean.core.BeanDefineOriginal;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectConcat;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelReflect;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Basis
@Bean
public class InjectBeanFactory implements IBeanFactorySupport, IBeanDefineSupply, IBeanDefineAware,
		IBeanObjectProcessor, IBeanFactoryAware, IAdapterSupport, IBeanFactoryStarted, IBeanFactoryStopping {

	/** Instance */
	private static InjectBeanFactory Instance;

	/**
	 * @return the Instance
	 */
	public static InjectBeanFactory getInstance() {
		return Instance;
	}

	/** beanDefining */
	private boolean beanDefining;

	/** methodDefines */
	private IMethodDefine[] methodDefines;

	/**
	 * 
	 */
	public InjectBeanFactory(BeanFactory beanFactory) {
		Instance = this;
		beanDefining = true;
		methodDefines = KernelCollection.toArray(beanFactory.getBeanObjects(IMethodDefine.class), IMethodDefine.class);
	}

	/** typeSupports */
	@Inject(type = InjectType.Selectable)
	private ITypeSupport[] typeSupports = new ITypeSupport[] {};

	/** fieldSupports */
	private IFieldSupport[] fieldSupports = new IFieldSupport[] { new IFieldSupport() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.bean.inject.IFieldSupport#getInjectInvoker(com.absir.bean
		 * .basis.BeanScope, com.absir.bean.basis.BeanDefine,
		 * java.lang.reflect.Field)
		 */
		@Override
		public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Field field) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				return InjectFieldOrders.getInjectField(field, inject.value(), inject.type(),
						field.getAnnotation(Orders.class));
			}

			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				return new InjectValue(field, value);
			}

			if (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType())) {
				InjectConcat injectConcat = field.getAnnotation(InjectConcat.class);
				if (injectConcat != null) {
					return new InjectArrayConcat(field, injectConcat);
				}
			}

			return null;
		}
	},

	};

	/** methodSupports */
	private IMethodSupport[] methodSupports = new IMethodSupport[] { new IMethodSupport<Object[]>() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.bean.inject.IMethodSupport#getInject(com.absir.bean.basis
		 * .BeanScope, com.absir.bean.basis.BeanDefine,
		 * java.lang.reflect.Method)
		 */
		@Override
		public Object[] getInject(BeanScope beanScope, BeanDefine beanDefine, Method method) {
			int length = methodInjects.length;
			Object[] injects = new Object[length + 3];
			boolean injected = false;
			boolean required = true;
			for (int i = 0; i < length; i++) {
				IMethodInject methodInject = methodInjects[i];
				Object inject = methodInject.getInjects(beanScope, beanDefine, method);
				if (inject != null) {
					injects[i] = inject;
					if (!injected) {
						injected = true;
					}

					if (required && !methodInject.isRequired()) {
						required = false;
					}
				}
			}

			Inject inject = method.getAnnotation(Inject.class);
			if (inject == null && !injected) {
				return null;
			}

			injects[length] = required;
			injects[length + 1] = inject;
			injects[length + 2] = method.getAnnotation(InjectOrder.class);
			return injects;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.absir.bean.inject.IMethodSupport#getInjectInvoker(java.lang.
		 * Object , java.lang.reflect.Method, java.lang.reflect.Method,
		 * java.lang.Object, java.util.Map)
		 */
		@Override
		public InjectInvoker getInjectInvoker(Object[] injects, Method method, Method beanMethod, Object beanObject,
				Map<Method, Set<Object>> methodMapInjects) {
			int length = methodInjects.length;
			boolean required = (Boolean) injects[length];
			Inject inject = (Inject) injects[length + 1];
			String value = inject == null ? null : inject.value();
			InjectType type = inject == null ? required ? InjectType.Required : InjectType.Selectable : inject.type();
			InjectOrder injectOrder = (InjectOrder) injects[length + 2];
			InjectMethod injectMethod = injectOrder == null || injectOrder.value() == 0
					? new InjectMethod(method, beanMethod, value, type)
					: new InjectMethodOrder(method, beanMethod, value, type, injectOrder.value());
			for (int i = 0; i < length; i++) {
				Object inj = injects[i];
				if (inj != null) {
					IMethodInject methodInject = methodInjects[i];
					if (methodMapInjects != null) {
						Set<Object> mInjects = methodMapInjects.get(beanMethod);
						if (mInjects == null) {
							mInjects = new HashSet<Object>();
							mInjects.add(methodInject);
							methodMapInjects.put(beanMethod, mInjects);

						} else if (!mInjects.add(methodInject)) {
							continue;
						}
					}

					try {
						methodInject.setInjectMethod(inj, method, beanObject, injectMethod);

					} catch (Throwable e) {
						throw new RuntimeException(
								"Can not inject " + methodInjects[i] + "=>" + beanObject + '.' + method, e);
					}
				}
			}

			return inject == null ? null : injectMethod;
		}
	},

	};

	/** methodInjects */
	private IMethodInject[] methodInjects = new IMethodInject[] { new IMethodInject<Started>() {

		@Override
		public boolean isRequired() {
			return true;
		}

		@Override
		public Started getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
			return method.getAnnotation(Started.class);
		}

		@Override
		public void setInjectMethod(Started inject, Method method, Object beanObject, InjectMethod injectMethod) {
			startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectMethod));
		}

	},

			new IMethodInject<Stopping>() {

				@Override
				public boolean isRequired() {
					return true;
				}

				@Override
				public Stopping getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
					return method.getAnnotation(Stopping.class);
				}

				@Override
				public void setInjectMethod(Stopping inject, Method method, Object beanObject,
						InjectMethod injectMethod) {
					stoppingInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectMethod));
				}
			},

	};

	/** beanMethods */
	private Map<BeanMethod, Method> beanMethods = new HashMap<BeanMethod, Method>();

	/**
	 * @param beanType
	 * @param method
	 * @return
	 */
	public Method getBeanMethod(Class<?> beanType, Method method) {
		if (Modifier.isPrivate(method.getModifiers()) || Modifier.isFinal(method.getModifiers())) {
			method.setAccessible(true);
			return method;
		}

		if (beanDefining) {
			BeanMethod beanMethod = new BeanMethod(beanType, method);
			method = beanMethods.get(beanMethod);
			if (method == null) {
				synchronized (beanMethods) {
					method = beanMethods.get(beanMethod);
					if (method == null) {
						method = KernelReflect.declaredMethod(beanMethod.getBeanType(),
								beanMethod.getMethod().getName(), beanMethod.getMethod().getParameterTypes());
						if (method == null) {
							return null;
						}

						beanMethods.put(beanMethod, method);
					}
				}
			}

		} else {
			method = KernelReflect.declaredMethod(beanType, method.getName(), method.getParameterTypes());
		}

		return method;
	}

	/**
	 * @param fieldSupports
	 */
	@Inject(type = InjectType.Selectable)
	private void setFieldSupports(IFieldSupport[] fieldSupports) {
		this.fieldSupports = KernelArray.concat(this.fieldSupports, fieldSupports);
	}

	/**
	 * @param methodSupports
	 */
	@Inject(type = InjectType.Selectable)
	private void setMethodSupports(IMethodSupport[] methodSupports) {
		this.methodSupports = KernelArray.concat(this.methodSupports, methodSupports);
	}

	/**
	 * @param methodInjects
	 */
	@Inject(type = InjectType.Selectable)
	private void setMethodSupports(IMethodInject[] methodInjects) {
		this.methodInjects = KernelArray.concat(this.methodInjects, methodInjects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactorySupport#supports(com.absir.bean.core
	 * .BeanFactoryImpl)
	 */
	@Override
	public boolean supports(BeanFactoryImpl beanFactory) {
		beanDefining = true;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 0;
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
		Bean bean = beanType.getAnnotation(Bean.class);
		List<BeanDefine> beanDefines = null;
		if (bean != null || beanType.getAnnotation(Configure.class) != null) {
			BeanDefine beanDefine = bean == null || BeanDefineOriginal.isAbstractBeanType(beanType) ? null
					: new InjectBeanDefine(new BeanDefineType(bean.value(), beanType), bean.scope());
			beanDefines = getBeanDefines(beanFactory, beanType, beanDefine);
		}

		if (beanDefines == null || beanDefines.isEmpty()) {
			if (beanType.getAnnotation(Inject.class) != null) {
				startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(null, new InjectBeanType(beanType)));
			}
		}

		return beanDefines;
	}

	/**
	 * @param beanFactory
	 * @param beanType
	 * @param beanDefine
	 * @return
	 */
	public List<BeanDefine> getBeanDefines(final BeanFactoryImpl beanFactory, final Class<?> beanType,
			final BeanDefine beanDefine) {
		final List<BeanDefine> beanDefines = new ArrayList<BeanDefine>();
		if (beanDefine != null) {
			beanDefines.add(BeanDefineObject.getBeanDefine(beanType, beanDefine));
		}

		KernelReflect.doWithDeclaredFields(beanType, new CallbackBreak<Field>() {

			@Override
			public void doWith(Field template) throws BreakException {
				if (Modifier.isStatic(template.getModifiers()) && !Modifier.isFinal(template.getModifiers())
						&& template.getAnnotations().length > 0) {
					InjectAdapter.inject(template);
				}
			}
		});

		final Set<Method> beanMethodSet = new HashSet<Method>();
		KernelReflect.doWithDeclaredMethods(beanType, new CallbackBreak<Method>() {

			@Override
			public void doWith(Method template) throws BreakException {
				Method beanMethod = null;
				Bean bean = null;
				if (Modifier.isStatic(template.getModifiers())) {
					bean = template.getAnnotation(Bean.class);
					if (bean == null) {
						if (template.getAnnotations().length > 0) {
							InjectAdapter.inject(template);
						}

						return;
					}

				} else {
					for (IMethodDefine methodDefine : methodDefines) {
						Object define = methodDefine.getDefine(beanType, template, beanDefine);
						if (define != null) {
							if (beanMethod == null) {
								beanMethod = getBeanMethod(beanType, template);
								if (beanMethod == null) {
									return;

								} else {
									if (!beanMethodSet.add(beanMethod)) {
										return;
									}
								}
							}

							methodDefine.setDefine(define, beanType, beanMethod, template, beanDefine);
						}
					}
				}

				if (bean == null) {
					bean = template.getAnnotation(Bean.class);
					if (bean == null) {
						return;
					}
				}

				String beanName = BeanDefineMethod.getBeanName(bean.value(), template);
				BeanDefine beanDefineMethod = beanFactory.getBeanDefine(beanName);
				if (beanDefineMethod == null
						|| !template.getReturnType().isAssignableFrom(beanDefineMethod.getBeanType())) {
					if (beanMethod == null) {
						beanMethod = getBeanMethod(beanType, template);
						if (beanMethod == null) {
							return;

						} else {
							if (!beanMethodSet.add(beanMethod)) {
								return;
							}
						}
					}

					beanDefineMethod = new InjectBeanDefine((new BeanDefineMethod(beanName, beanDefine, beanMethod)),
							bean.scope());
					beanDefines.add(beanDefineMethod);
				}
			}
		});

		return beanDefines;
	}

	/**
	 * @param beanType
	 * @param methodEntry
	 */
	public void getMethodEntries(final Class<?> beanType, final IMethodEntry methodEntry) {
		final Set<Method> beanMethodSet = new HashSet<Method>();
		KernelReflect.doWithDeclaredMethods(beanType, new CallbackBreak<Method>() {

			@Override
			public void doWith(Method template) throws BreakException {
				if (!Modifier.isStatic(template.getModifiers())) {
					Object define = methodEntry.getMethod(beanType, template);
					if (define != null) {
						Method beanMethod = getBeanMethod(beanType, template);
						if (beanMethod != null) {
							if (!beanMethodSet.add(beanMethod)) {
								return;
							}

							methodEntry.setMethodEntry(define, beanType, beanMethod, template);
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineAware#registerBeanDefine(com.absir.bean
	 * .core.BeanFactory, com.absir.bean.basis.IBeanDefine)
	 */
	@Override
	public void registerBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
		changedBeanDefine(beanFactory, beanDefine, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineAware#unRegisterBeanDefine(com.absir
	 * .bean.core.BeanFactory, com.absir.bean.basis.IBeanDefine)
	 */
	@Override
	public void unRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
		changedBeanDefine(beanFactory, beanDefine, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanDefineAware#replaceRegisterBeanDefine(com.
	 * absir.bean.core.BeanFactoryImpl, com.absir.bean.basis.BeanDefine)
	 */
	@Override
	public void replaceRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
		changedBeanDefine(beanFactory, beanDefine, true);
	}

	/** injectObservers */
	private final Set<InjectObserver> injectObservers = Collections
			.newSetFromMap(new ConcurrentHashMap<InjectObserver, Boolean>());

	/** injectObserverObjects */
	private final Set<InjectObserverObject> injectObserverObjects = Collections
			.newSetFromMap(new ConcurrentHashMap<InjectObserverObject, Boolean>());

	/** injectInvokerObserverSoftCaches */
	private final Map<InjectObserverSoftObject, List<SoftReference<Object>>> injectInvokerObserverSoftCaches = new ConcurrentHashMap<InjectObserverSoftObject, List<SoftReference<Object>>>();

	/**
	 * @param beanFactory
	 * @param beanDefine
	 * @param register
	 */
	private void changedBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine, boolean register) {
		if (injectObservers.size() > 0) {
			for (InjectObserver injectObserver : injectObservers) {
				injectObserver.changed(beanFactory, beanDefine, null, register);
			}
		}

		if (injectObserverObjects.size() > 0) {
			for (InjectObserverObject injectObserverObject : injectObserverObjects) {
				injectObserverObject.changed(beanFactory, beanDefine, register);
			}
		}

		if (injectInvokerObserverSoftCaches.size() > 0) {
			for (Entry<InjectObserverSoftObject, List<SoftReference<Object>>> entry : injectInvokerObserverSoftCaches
					.entrySet()) {
				entry.getKey().changed(beanFactory, beanDefine, entry.getValue(), register);
			}
		}
	}

	/** injectInvokerCaches */
	private final Map<Class<?>, Object[]> injectInvokerCaches = new HashMap<Class<?>, Object[]>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanObjectProcessor#processBeanObject(com.absir
	 * .bean.basis.BeanFactory, com.absir.bean.basis.BeanScope,
	 * com.absir.bean.basis.BeanDefine, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void processBeanObject(final BeanFactory beanFactory, final BeanScope beanScope, final BeanDefine beanDefine,
			final Object beanObject, final Object beanProxy) {
		final Class<?> beanType = beanObject.getClass();
		Object[] injectInvokers = beanScope == BeanScope.PROTOTYPE ? injectInvokerCaches.get(beanType) : null;
		if (injectInvokers == null) {
			synchronized (beanType) {
				if (beanScope == BeanScope.PROTOTYPE) {
					injectInvokers = injectInvokerCaches.get(beanType);
				}

				if (injectInvokers == null) {
					final Map<Method, Set<IMethodSupport>> methodMapSupports = new HashMap<Method, Set<IMethodSupport>>();
					final Map<Method, Set<Object>> methodMapInjects = new HashMap<Method, Set<Object>>();
					final List<InjectInvoker> iInvokerList = new ArrayList<InjectInvoker>();
					final List<InjectInvoker> iInvokerScope = new ArrayList<InjectInvoker>();
					final List<InjectInvoker> pInvokerList = new ArrayList<InjectInvoker>();
					final List<InjectInvoker> pInvokerScope = new ArrayList<InjectInvoker>();
					KernelReflect.doWithClasses(beanType, new CallbackBreak<Class<?>>() {

						@Override
						public void doWith(Class<?> template) throws BreakException {
							if (typeSupports != null) {
								for (ITypeSupport typeSupport : typeSupports) {
									InjectInvoker injectInvoker = typeSupport.getInjectInvoker(beanScope, beanDefine,
											template);
									if (injectInvoker != null) {
										iInvokerScope.add(injectInvoker);
									}
								}
							}

							for (Field field : template.getDeclaredFields()) {
								if (!(Modifier.isStatic(field.getModifiers())
										|| Modifier.isFinal(field.getModifiers()))) {
									field.setAccessible(true);
									for (IFieldSupport fieldSupport : fieldSupports) {
										InjectInvoker injectInvoker = fieldSupport.getInjectInvoker(beanScope,
												beanDefine, field);
										if (injectInvoker != null) {
											iInvokerScope.add(injectInvoker);
										}
									}
								}
							}

							for (Method method : template.getDeclaredMethods()) {
								if (!Modifier.isStatic(method.getModifiers())) {
									Method beanMethod = null;
									for (IMethodSupport methodSupport : methodSupports) {
										boolean proxy = false;
										Object inject = methodSupport.getInject(beanScope, beanDefine, method);
										if (inject != null) {
											if (beanMethod == null) {
												method = getBeanMethod(beanType, method);
												if (method == null) {
													break;
												}

												beanMethod = getBeanMethod(beanProxy.getClass(), method);
												if (beanMethod == null) {
													beanMethod = method;

												} else {
													proxy = true;
												}
											}

											Set<IMethodSupport> supports = methodMapSupports.get(beanMethod);
											if (supports == null) {
												supports = new HashSet<IMethodSupport>();
												supports.add(methodSupport);
												methodMapSupports.put(beanMethod, supports);

											} else {
												if (!supports.add(methodSupport)) {
													continue;
												}
											}

											InjectInvoker injectInvoker = methodSupport.getInjectInvoker(inject, method,
													beanMethod, proxy ? beanProxy : beanObject, methodMapInjects);
											if (injectInvoker != null) {
												if (proxy) {
													pInvokerScope.add(injectInvoker);

												} else {
													iInvokerScope.add(injectInvoker);
												}
											}
										}
									}
								}
							}

							iInvokerList.addAll(0, iInvokerScope);
							iInvokerScope.clear();
							pInvokerList.addAll(0, pInvokerScope);
							pInvokerScope.clear();
						}
					});

					// 注入排序
					KernelList.sortOrderable(iInvokerList);
					KernelList.sortOrderable(pInvokerList);
					int iSize = iInvokerList.size();
					int pSize = pInvokerList.size();
					injectInvokers = iSize == 0 && pSize == 0 ? KernelLang.NULL_OBJECTS
							: new Object[] { iSize == 0 ? KernelLang.NULL_LIST_SET : iInvokerList,
									pSize == 0 ? KernelLang.NULL_LIST_SET : pInvokerList };
					if (beanScope == BeanScope.PROTOTYPE) {
						injectInvokerCaches.put(beanType, injectInvokers);
						InjectObserverSoftObject injectObserverSoftObject = new InjectObserverSoftObject(beanType);
						for (InjectInvoker injectInvoker : iInvokerList) {
							if (injectInvoker instanceof InjectInvokerObserver) {
								injectObserverSoftObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
							}
						}

						if (injectObserverSoftObject.injectObservers != null) {
							injectInvokerObserverSoftCaches.put(injectObserverSoftObject,
									new LinkedList<SoftReference<Object>>());
						}

						injectObserverSoftObject = new InjectObserverSoftObject(beanProxy.getClass());
						for (InjectInvoker injectInvoker : pInvokerList) {
							if (injectInvoker instanceof InjectInvokerObserver) {
								injectObserverSoftObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
							}
						}

						if (injectObserverSoftObject.injectObservers != null) {
							injectInvokerObserverSoftCaches.put(injectObserverSoftObject,
									new LinkedList<SoftReference<Object>>());
						}
					}
				}
			}
		}

		// 清除缓存
		BeanDefineDiscover.clear();

		if (injectInvokers != KernelLang.NULL_OBJECTS) {
			for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[0]) {
				injectInvoker.invoke(beanFactory, beanObject);
			}

			for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[1]) {
				injectInvoker.invoke(beanFactory, beanProxy);
			}
		}

		if (beanScope == BeanScope.PROTOTYPE) {
			List<SoftReference<Object>> beanObjects = injectInvokerObserverSoftCaches.get(beanType);
			if (beanObjects != null) {
				synchronized (beanObjects) {
					beanObjects.add(new SoftReference<Object>(beanObject));
				}
			}

			beanObjects = injectInvokerObserverSoftCaches.get(beanProxy.getClass());
			if (beanObjects != null) {
				synchronized (beanObjects) {
					beanObjects.add(new SoftReference<Object>(beanProxy));
				}
			}

		} else {
			if (!injectObserverObjects.contains(beanObject)) {
				if (injectInvokers != KernelLang.NULL_OBJECTS) {
					InjectObserverObject injectObserverObject = new InjectObserverObject(beanObject);
					for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[0]) {
						if (injectInvoker instanceof InjectInvokerObserver) {
							injectObserverObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
						}
					}

					if (injectObserverObject.injectObservers != null) {
						injectObserverObjects.add(injectObserverObject);
					}

					injectObserverObject = new InjectObserverObject(beanProxy);
					for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[1]) {
						if (injectInvoker instanceof InjectInvokerObserver) {
							injectObserverObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
						}
					}

					if (injectObserverObject.injectObservers != null) {
						injectObserverObjects.add(injectObserverObject);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.IAdapterSupport#adapter(com.absir.bean.basis.
	 * BeanFactory, java.lang.Object, java.util.Collection,
	 * java.util.Collection)
	 */
	@Override
	public void adapter(BeanFactory beanFactory, Object beanObject, Collection<Field> fields,
			Collection<Method> methods) {
		BeanDefine beanDefine = new BeanDefineOriginal(new BeanDefineType(InjectAdapter.class));
		InjectAdapter injectAdapter = (InjectAdapter) beanDefine.getBeanObject(beanFactory);
		for (Field field : fields) {
			for (IFieldSupport fieldSupport : fieldSupports) {
				InjectInvoker injectInvoker = fieldSupport.getInjectInvoker(BeanScope.SINGLETON, beanDefine, field);
				if (injectInvoker != null) {
					injectInvoker.invoke(beanFactory, beanObject);
					if (injectInvoker instanceof InjectInvokerObserver) {
						InjectObserver injectObserver = ((InjectInvokerObserver) injectInvoker).getInjectObserver();
						if (injectObserver != null) {
							injectObservers.add(injectObserver);
						}
					}
				}
			}
		}

		for (Method method : methods) {
			for (IMethodSupport methodSupport : methodSupports) {
				Object inject = methodSupport.getInject(BeanScope.SINGLETON, beanDefine, method);
				if (inject != null) {
					InjectInvoker injectInvoker = methodSupport.getInjectInvoker(inject, method, method, injectAdapter,
							null);
					if (injectInvoker != null) {
						injectInvoker.invoke(beanFactory, beanObject);
						if (injectInvoker instanceof InjectInvokerObserver) {
							InjectObserver injectObserver = ((InjectInvokerObserver) injectInvoker).getInjectObserver();
							if (injectObserver != null) {
								injectObservers.add(injectObserver);
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
	 * com.absir.android.bean.config.IBeanFactoryAware#beforeRegister(com.absir
	 * .android.bean.core.BeanFactory)
	 */
	@Override
	public void beforeRegister(BeanFactoryImpl beanFactory) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.android.bean.config.IBeanFactoryAware#started(com.absir.android
	 * .bean.BeanFactory)
	 */
	@Override
	public void afterRegister(BeanFactoryImpl beanFactory) {
		InjectAdapter injectAdapter = InjectAdapter.getInstance();
		if (injectAdapter != null) {
			for (IAdapterSupport adapterSupport : (List<IAdapterSupport>) (List) beanFactory
					.getBeanObjects(IAdapterSupport.class)) {
				adapterSupport.adapter(beanFactory, injectAdapter, injectAdapter.fields, injectAdapter.methods);
			}
		}

		InjectAdapter.clear();

		for (BeanDefine beanDefine : beanFactory.getBeanDefines()) {
			if (beanDefine.getBeanScope() == BeanScope.SINGLETON) {
				beanDefine.getBeanObject(beanFactory);
			}
		}
	}

	/** startedInjectInvokers */
	private List<Entry<Object, InjectInvoker>> startedInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();

	/** stoppingInjectInvokers */
	private List<Entry<Object, InjectInvoker>> stoppingInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();

	/**
	 * @param beanObject
	 * @param injectInvoker
	 */
	public void addStated(Object beanObject, InjectInvoker injectInvoker) {
		if (beanDefining) {
			startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectInvoker));
		}
	}

	/**
	 * @param beanObject
	 * @param injectInvoker
	 */
	public void addStopping(Object beanObject, InjectInvoker injectInvoker) {
		stoppingInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectInvoker));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryStarted#started(com.absir.bean.basis
	 * .BeanFactory)
	 */
	@Override
	public void started(BeanFactory beanFactory) {
		if (startedInjectInvokers.size() > 0) {
			List<Entry<Object, InjectInvoker>> injectInvokers = startedInjectInvokers;
			startedInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();
			Collections.sort(injectInvokers, OBJECT_INVOKER_COMPARATOR);
			for (Entry<Object, InjectInvoker> entry : injectInvokers) {
				try {
					entry.getValue().invoke(beanFactory, entry.getKey());

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		beanMethods.clear();
		beanDefining = false;
		BeanDefineDiscover.clear();
		beanFactory.unRegisterBeanType(InjectOnce.class);
		beanFactory.unRegisterWithoutBeanType(InjectRetain.class, IBeanDefineSupply.class, IBeanDefineAware.class,
				IBeanDefineProcessor.class, IBeanObjectProcessor.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryStopping#stopping(com.absir.bean.basis
	 * .BeanFactory)
	 */
	@Override
	public void stopping(BeanFactory beanFactory) {
		Environment.setActive(false);
		if (stoppingInjectInvokers.size() > 0) {
			List<Entry<Object, InjectInvoker>> injectInvokers = stoppingInjectInvokers;
			stoppingInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();
			Collections.sort(injectInvokers, OBJECT_INVOKER_COMPARATOR);
			for (Entry<Object, InjectInvoker> entry : injectInvokers) {
				try {
					entry.getValue().invoke(beanFactory, entry.getKey());

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		Environment.setStarted(false);
	}

	/** OBJECT_INVOKER_COMPARATOR */
	protected static final Comparator<Entry<Object, InjectInvoker>> OBJECT_INVOKER_COMPARATOR = new Comparator<Entry<Object, InjectInvoker>>() {

		@Override
		public int compare(Entry<Object, InjectInvoker> o1, Entry<Object, InjectInvoker> o2) {
			return o1.getValue().getOrder() - o2.getValue().getOrder();
		}
	};
}
