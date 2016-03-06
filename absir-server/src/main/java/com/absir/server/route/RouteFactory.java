/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-20 上午10:50:39
 */
package com.absir.server.route;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.core.BeanDefineDiscover;
import com.absir.bean.core.BeanDefineOriginal;
import com.absir.bean.core.BeanDefineType;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.IMethodEntry;
import com.absir.bean.inject.InjectBeanDefine;
import com.absir.bean.inject.InjectBeanFactory;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.kernel.KernelString;
import com.absir.property.PropertyUtils;
import com.absir.server.in.InMethod;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnScope;
import com.absir.server.route.entity.EntityMutil;
import com.absir.server.route.entity.EntityOnPut;
import com.absir.server.route.entity.EntitySingleton;
import com.absir.server.route.parameter.ParameterResolver;
import com.absir.server.route.parameter.ParameterResolverPath;
import com.absir.server.route.returned.ReturnedResolver;
import com.absir.server.value.After;
import com.absir.server.value.Before;
import com.absir.server.value.Close;
import com.absir.server.value.Interceptors;
import com.absir.server.value.Mapping;
import com.absir.server.value.NoBody;
import com.absir.server.value.Nullable;
import com.absir.server.value.OnException;
import com.absir.server.value.Server;
import com.absir.server.value.UrlBase;
import com.absir.server.value.UrlDecode;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Basis
@Bean
public class RouteFactory implements IBeanDefineSupply, IBeanFactoryAware, IMethodEntry<Object> {

	/** ME */
	public static final RouteFactory ME = BeanFactoryUtils.get(RouteFactory.class);

	@Inject
	RouteMapping routeMapping;

	@Inject(type = InjectType.Selectable)
	@Orders
	ParameterResolver[] parameterResolvers;

	/** parameterResolverPath */
	ParameterResolverPath parameterResolverPath = new ParameterResolverPath();

	@Inject(type = InjectType.Selectable)
	@Orders
	ReturnedResolver[] returnedResolvers;

	/** methodMapRouteMethod */
	private Map<Method, RouteMethod> methodMapRouteMethod = new HashMap<Method, RouteMethod>();

	/** beanTypeMapRouteEntry */
	private Map<Class<?>, RouteEntry> beanTypeMapRouteEntry = new HashMap<Class<?>, RouteEntry>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return -1;
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
		if (!BeanDefineOriginal.isAbstractBeanType(beanType)) {
			Server server = beanType.getAnnotation(Server.class);
			if (server != null) {
				RouteDefine routeDefine = new RouteDefine(
						new InjectBeanDefine(new BeanDefineType(beanType),
								server.scope() == OnScope.SINGLETON ? BeanScope.SINGLETON : BeanScope.PROTOTYPE),
						server.scope());
				return InjectBeanFactory.getInstance().getBeanDefines(beanFactory, beanType, routeDefine);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.IMethodFactory#getMethod(java.lang.Class,
	 * java.lang.reflect.Method)
	 */
	@Override
	public Object getMethod(Class<?> beanType, Method method) {
		Before before = method.getAnnotation(Before.class);
		if (before != null) {
			return before;
		}

		After after = method.getAnnotation(After.class);
		if (after != null) {
			return after;
		}

		OnException onException = method.getAnnotation(OnException.class);
		if (onException != null && onException.value().length > 0) {
			return onException;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.IMethodEntry#setMethodEntry(java.lang.Object,
	 * java.lang.Class, java.lang.reflect.Method, java.lang.reflect.Method)
	 */
	@Override
	public void setMethodEntry(Object define, Class<?> beanType, Method beanMethod, Method method) {
		RouteEntry routeEntry = beanTypeMapRouteEntry.get(beanType);
		if (routeEntry == null) {
			routeEntry = new RouteEntry();
			beanTypeMapRouteEntry.put(beanType, routeEntry);
		}

		Class<?> beanClass = beanMethod.getDeclaringClass();
		if (beanType != beanClass) {
			RouteEntry routeMethodEntry = beanTypeMapRouteEntry.get(beanClass);
			if (routeMethodEntry == null) {
				routeMethodEntry = new RouteEntry();
				beanTypeMapRouteEntry.put(beanClass, routeMethodEntry);
			}

			routeEntry.addRouteEntries(routeMethodEntry);
			routeEntry = routeMethodEntry;
		}

		if (methodMapRouteMethod.get(beanMethod) == null) {
			RouteMethod routeMethod = getRouteMethod(beanType, beanMethod, method);
			methodMapRouteMethod.put(beanMethod, routeMethod);
			if (define instanceof Before) {
				routeEntry.addBeforeMethod(routeMethod);

			} else if (define instanceof After) {
				routeEntry.addAfterMethod(routeMethod);

			} else if (define instanceof OnException) {
				routeEntry.addRouteException(new RouteException(((OnException) define).value(), routeMethod));
			}
		}
	}

	/**
	 * @param beanType
	 * @return
	 */
	protected RouteEntry getRouteEntry(Class<?> beanType) {
		RouteEntry routeEntry = beanTypeMapRouteEntry.get(beanType);
		if (routeEntry == null) {
			routeEntry = new RouteEntry();
			beanTypeMapRouteEntry.put(beanType, routeEntry);
			Class<?> beanClass = beanType;
			Interceptors interceptors;
			while (beanClass != null && beanClass != Object.class) {
				interceptors = beanClass.getAnnotation(Interceptors.class);
				if (interceptors != null) {
					for (Class<? extends Interceptor> interceptorClass : interceptors.value()) {
						routeEntry.addInterceptor(BeanFactoryUtils.getRegisterBeanObject(interceptorClass));
					}
				}

				beanClass = beanClass.getSuperclass();
			}

			InjectBeanFactory.getInstance().getMethodEntries(beanType, this);
			routeEntry = routeEntry.getRouteEntry();
		}

		return routeEntry;
	}

	/**
	 * @param beanType
	 * @param beanMethod
	 * @param method
	 * @return
	 */
	protected RouteMethod getRouteMethod(Class<?> beanType, Method beanMethod, Method method) {
		return getRouteMethod(beanType, beanMethod, method, null, null, null);
	}

	/**
	 * @param beanType
	 * @param beanMethod
	 * @param method
	 * @param parameterPathNames
	 * @param parameterPathIndexs
	 * @param parameterPathAnnotations
	 * @return
	 */
	protected RouteMethod getRouteMethod(Class<?> beanType, Method beanMethod, Method method,
			List<String> parameterPathNames, List<Integer> parameterPathIndexs,
			List<Annotation[]> parameterPathAnnotations) {
		RouteMethod routeMethod = new RouteMethod(beanMethod);
		Annotation[][] parameterAnnotations = beanMethod.getParameterAnnotations();
		int length = parameterAnnotations.length;
		String[] parameterNames = BeanDefineDiscover.paramterNames(beanMethod, parameterAnnotations);
		routeMethod.parameterTypes = beanMethod.getParameterTypes();
		routeMethod.parameters = new Object[length];
		routeMethod.parameterResolvers = new ParameterResolver[length];
		routeMethod.beanNames = PropertyUtils.paramterBeanNames(parameterAnnotations);
		routeMethod.nullables = new boolean[length];
		routeMethod.noBody = (method == null ? beanMethod : method).getAnnotation(NoBody.class) != null;
		for (int i = 0; i < length; i++) {
			Object parameter = null;
			if (parameterResolvers != null) {
				for (ParameterResolver parameterResolver : parameterResolvers) {
					parameter = parameterResolver.getParameter(i, parameterNames, routeMethod.parameterTypes,
							parameterAnnotations, beanMethod);
					if (parameter != null) {
						routeMethod.parameters[i] = parameter;
						routeMethod.parameterResolvers[i] = parameterResolver;
						break;
					}
				}
			}

			if (parameterPathAnnotations != null
					&& KernelArray.getAssignable(parameterAnnotations[i], Nullable.class) != null) {
				routeMethod.nullables[i] = true;
			}

			if (parameter == null) {
				parameter = parameterResolverPath.getParameter(i, parameterNames, routeMethod.parameterTypes,
						parameterAnnotations, beanMethod);
				if (parameterPathNames != null) {
					parameterPathNames.add((String) parameter);
				}

				if (parameterPathIndexs != null) {
					parameterPathIndexs.add(i);
				}

				if (parameterPathAnnotations != null) {
					parameterPathAnnotations.add(parameterAnnotations[i]);
				}

				routeMethod.parameters[i] = parameter;
				routeMethod.parameterResolvers[i] = parameterResolverPath;
			}
		}

		if (returnedResolvers != null) {
			while (beanMethod != null) {
				for (ReturnedResolver returnedResolver : returnedResolvers) {
					routeMethod.returned = returnedResolver.getReturned(beanMethod);
					if (routeMethod.returned != null) {
						routeMethod.returnedResolver = returnedResolver;
						return routeMethod;
					}
				}

				beanMethod = beanMethod == method ? null : method;
			}

			while (beanType != null && beanType != Object.class) {
				for (ReturnedResolver returnedResolver : returnedResolvers) {
					routeMethod.returned = returnedResolver.getReturned(beanType);
					if (routeMethod.returned != null) {
						routeMethod.returnedResolver = returnedResolver;
						return routeMethod;
					}
				}

				beanType = beanType.getSuperclass();
			}
		}

		return routeMethod;
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
		List<BeanDefine> beanDefines = beanFactory.getBeanDefineList(RouteDefine.class);
		int size = beanDefines.size();
		for (int i = 0; i < size; i++) {
			BeanDefine beanDefine = beanDefines.get(i);
			if (beanDefine.getBeanType().getAnnotation(Close.class) != null) {
				continue;
			}

			RouteDefine routeDefine = BeanFactoryImpl.getBeanDefine(beanDefine, RouteDefine.class);
			Object beanObject = beanDefine.getBeanObject(beanFactory);
			beanDefine = beanFactory.getBeanDefine(beanDefine.getBeanName());
			beanDefines.set(i, beanDefine);
			Set<Method> beanMethods = new HashSet<Method>();
			if (beanObject != null) {
				String name = null;
				RouteEntry routeEntry = null;
				RouteEntity routeEntity = null;
				IRoute iRoute = null;
				Mapping mapping = null;
				boolean urlDecode = false;
				Class<?> beanType = beanDefine.getBeanType();
				Class<?> beanClass = beanType;
				while (beanClass != null && beanClass != Object.class) {
					for (Method method : beanClass.getDeclaredMethods()) {
						if (isMethodServering(method)) {
							if (name == null) {
								name = KernelString.lastString(beanDefine.getBeanType().getSimpleName(), '_');
								routeEntry = getRouteEntry(beanType);
								routeEntity = getRouteEntity(beanObject, beanDefine, routeDefine);
								iRoute = routeEntry == null ? null : routeEntry.getIRoute();
								if (iRoute == null) {
									iRoute = routeMapping;
								}

								Class<?> routeClass = beanType;
								while (routeClass != null && routeClass != Object.class) {
									mapping = routeClass.getAnnotation(Mapping.class);
									if (mapping != null) {
										break;
									}

									routeClass = routeClass.getSuperclass();
								}

								routeClass = beanType;
								while (routeClass != null && routeClass != Object.class) {
									if (routeClass.getAnnotation(UrlDecode.class) != null) {
										urlDecode = true;
										break;

									} else if (routeClass.getAnnotation(UrlBase.class) != null) {
										break;
									}

									routeClass = routeClass.getSuperclass();
								}
							}

							if (!beanMethods.add(InjectBeanFactory.getInstance().getBeanMethod(beanType, method))) {
								continue;
							}

							List<String> parameterPathNames = new ArrayList<String>();
							List<Integer> parameterPathIndexs = new ArrayList<Integer>();
							List<Annotation[]> parameterPathAnnotations = new ArrayList<Annotation[]>();
							RouteMethod routeMethod = getRouteMethod(beanType, method, null, parameterPathNames,
									parameterPathIndexs, parameterPathAnnotations);
							List<String> mappings = new ArrayList<String>();
							List<InMethod> inMethods = new ArrayList<InMethod>();
							iRoute.routeMapping(name, new ObjectEntry<Mapping, List<String>>(mapping, null), method,
									parameterPathNames, mappings, inMethods);
							if (mappings.size() > 0) {
								boolean url = (urlDecode || method.getAnnotation(UrlDecode.class) != null)
										&& method.getAnnotation(UrlBase.class) == null;
								String[] parameterPathNameArray = KernelCollection.toArray(parameterPathNames,
										String.class);
								routeMapping.routeMapping(
										new RouteAction(url, routeEntity, routeEntry, routeMethod,
												parameterPathNameArray, parameterPathIndexs, parameterPathAnnotations),
										parameterPathNameArray, mappings, inMethods);
							}
						}
					}

					beanClass = beanClass.getSuperclass();
				}
			}
		}

		// 完成路由，清除中间件
		for (BeanDefine beanDefine : beanDefines) {
			if (beanDefine != null) {
				beanFactory.unRegisterBeanDefine(beanDefine);
			}
		}

		routeMapping.getRouteAdapter().registerAllMatcher();
	}

	/**
	 * @param beanObject
	 * @param beanDefine
	 * @param routeDefine
	 * @return
	 */
	protected RouteEntity getRouteEntity(final Object beanObject, final BeanDefine beanDefine,
			RouteDefine routeDefine) {
		if (routeDefine.getOnScope() == OnScope.MULTI) {
			return new EntityMutil(beanDefine);
		}

		if (routeDefine.getOnScope() == OnScope.ONPUT) {
			return new EntityOnPut(beanDefine);
		}

		return new EntitySingleton(beanObject);
	}

	/**
	 * @param method
	 * @return
	 */
	public static boolean isMethodServering(Method method) {
		return Modifier.isPublic(method.getModifiers()) && !(Modifier.isStatic(method.getModifiers())
				|| method.getName().charAt(0) == '_' || method.getAnnotation(Close.class) != null);
	}
}
