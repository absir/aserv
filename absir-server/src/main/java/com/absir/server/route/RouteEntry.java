/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-23 下午4:28:08
 */
package com.absir.server.route;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.absir.core.kernel.KernelLang;
import com.absir.server.in.IDispatcher;
import com.absir.server.in.Input;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnPut;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RouteEntry {

	/** routeEntries */
	private List<RouteEntry> routeEntries;

	/** interceptors */
	protected List<Interceptor> interceptors;

	/** beforeMethods */
	private List<RouteMethod> beforeMethods;

	/** afterMethods */
	private List<RouteMethod> afterMethods;

	/** routeExceptions */
	private List<RouteException> routeExceptions;

	/**
	 * @return the routeEntries
	 */
	public List<RouteEntry> getRouteEntries() {
		return routeEntries;
	}

	/**
	 * @return the interceptors
	 */
	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	/**
	 * @return the beforeMethods
	 */
	public List<RouteMethod> getBeforeMethods() {
		return beforeMethods;
	}

	/**
	 * @return the afterMethods
	 */
	public List<RouteMethod> getAfterMethods() {
		return afterMethods;
	}

	/**
	 * @return the routeExceptions
	 */
	public List<RouteException> getRouteExceptions() {
		return routeExceptions;
	}

	/**
	 * @return
	 */
	protected RouteEntry getRouteEntry() {
		if (interceptors == null && beforeMethods == null && afterMethods == null && routeExceptions == null) {
			if (routeEntries == null || routeEntries.isEmpty()) {
				return null;
			}

			if (routeEntries.size() == 1) {
				return routeEntries.get(0).getRouteEntry();
			}
		}

		if (routeEntries != KernelLang.NULL_LIST_SET) {
			addRouteEntries(this, routeEntries);
			routeEntries = KernelLang.NULL_LIST_SET;

			if (beforeMethods != null) {
				beforeMethods = beforeMethods.isEmpty() ? null : Collections.unmodifiableList(beforeMethods);
			}

			if (afterMethods != null) {
				afterMethods = afterMethods.isEmpty() ? null : Collections.unmodifiableList(afterMethods);
			}

			if (routeExceptions != null) {
				routeExceptions = routeExceptions.isEmpty() ? null : Collections.unmodifiableList(routeExceptions);
			}
		}

		return this;
	}

	/**
	 * @param routeEntries
	 */
	private static void addRouteEntries(RouteEntry routeEntry, List<RouteEntry> routeEntries) {
		if (routeEntries != null) {
			for (RouteEntry entry : routeEntries) {
				if (entry.interceptors != null) {
					for (Interceptor interceptor : entry.interceptors) {
						routeEntry.addInterceptor(interceptor);
					}
				}

				if (entry.beforeMethods != null) {
					for (RouteMethod routeMethod : entry.beforeMethods) {
						routeEntry.addBeforeMethod(routeMethod);
					}
				}

				if (entry.afterMethods != null) {
					for (RouteMethod routeMethod : entry.afterMethods) {
						routeEntry.addAfterMethod(routeMethod);
					}
				}

				if (entry.routeExceptions != null) {
					for (RouteException routeException : entry.routeExceptions) {
						routeEntry.addRouteException(routeException);
					}
				}

				addRouteEntries(routeEntry, entry.routeEntries);
			}
		}
	}

	/**
	 * @param routeEntry
	 */
	protected void addRouteEntries(RouteEntry routeEntry) {
		if (routeEntries == null) {
			routeEntries = new ArrayList<RouteEntry>();

		} else if (routeEntries.contains(routeEntry)) {
			return;
		}

		routeEntries.add(routeEntry);
	}

	/**
	 * @param interceptor
	 */
	protected void addInterceptor(Interceptor interceptor) {
		if (interceptors == null) {
			interceptors = new ArrayList<Interceptor>();

		} else if (interceptors.contains(interceptor)) {
			return;
		}

		interceptors.add(interceptor);
	}

	/**
	 * @param routeMethod
	 */
	protected void addBeforeMethod(RouteMethod routeMethod) {
		if (beforeMethods == null) {
			beforeMethods = new ArrayList<RouteMethod>();
		}

		beforeMethods.add(routeMethod);
	}

	/**
	 * @param routeMethod
	 */
	protected void addAfterMethod(RouteMethod routeMethod) {
		if (afterMethods == null) {
			afterMethods = new ArrayList<RouteMethod>();
		}

		afterMethods.add(routeMethod);
	}

	/**
	 * @param routeException
	 */
	protected void addRouteException(RouteException routeException) {
		if (routeExceptions == null) {
			routeExceptions = new ArrayList<RouteException>();
		}

		routeExceptions.add(routeException);
	}

	/**
	 * @return
	 */
	public IRoute getIRoute() {
		if (interceptors != null) {
			for (Interceptor interceptor : interceptors) {
				if (interceptor instanceof IRoute) {
					return (IRoute) interceptor;
				}
			}
		}

		if (routeEntries != null) {
			for (RouteEntry routeEntry : routeEntries) {
				IRoute iRoute = routeEntry.getIRoute();
				if (iRoute != null) {
					return iRoute;
				}
			}
		}

		return null;
	}

	/**
	 * @param input
	 * @param iterator
	 * @return
	 * @throws Exception
	 */
	public OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable {
		if (iterator == null || !iterator.hasNext()) {
			return invoke(input, this);

		} else {
			return iterator.next().intercept(iterator, input);
		}
	}

	/**
	 * @param input
	 * @param routeEntry
	 * @return
	 * @throws Exception
	 */
	public static OnPut intercept(Input input, RouteEntry routeEntry) throws Throwable {
		if (routeEntry == null) {
			return invoke(input, routeEntry);

		} else {
			List<Interceptor> interceptors = routeEntry.interceptors;
			return routeEntry.intercept(interceptors == null ? null : interceptors.iterator(), input);
		}
	}

	/**
	 * @param input
	 * @param routeEntry
	 * @return
	 * @throws Throwable
	 */
	public static OnPut invoke(Input input, RouteEntry routeEntry) throws Throwable {
		IDispatcher dispatcher = input.getDispatcher();
		RouteAction routeAction = input.getRouteAction();
		if (dispatcher == null || routeAction == null) {
			return null;
		}

		Object routeBean = routeAction.getRouteEntity().getRouteBean(input);
		OnPut onPut = dispatcher.onPut(input, routeBean);
		try {
			onPut.open();
			invoke(routeBean, onPut, routeEntry);
			if (onPut.getReturnThrowable() != null) {
				throw onPut.getReturnThrowable();
			}

			dispatcher.resolveReturnedValue(routeBean, onPut);

		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}

			onPut.setReturnThrowable(e);
			if (!dispatcher.returnThrowable(e, routeBean, onPut)) {
				throw e;
			}

		} finally {
			OnPut.close();
		}

		return onPut;
	}

	/**
	 * @param routeBean
	 * @param onPut
	 * @param routeEntry
	 * @throws Throwable
	 */
	public static void invoke(Object routeBean, OnPut onPut, RouteEntry routeEntry) throws Throwable {
		if (routeEntry == null) {
			RouteAction routeAction = onPut.getInput().getRouteAction();
			if (routeAction != null) {
				routeAction.getRouteMethod().invoke(routeBean, onPut);
			}

		} else {
			List<RouteMethod> routeMethods = routeEntry.beforeMethods;
			if (routeMethods != null) {
				for (RouteMethod routeMethod : routeMethods) {
					routeMethod.invoke(routeBean, onPut);
				}
			}

			invoke(routeBean, onPut, null);
			routeMethods = routeEntry.afterMethods;
			if (routeMethods != null) {
				for (RouteMethod routeMethod : routeMethods) {
					routeMethod.invoke(routeBean, onPut);
				}
			}
		}
	}
}
