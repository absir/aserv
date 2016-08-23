/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-23 下午4:28:08
 */
package com.absir.server.route;

import com.absir.core.kernel.KernelLang;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.IDispatcher;
import com.absir.server.in.Input;
import com.absir.server.in.Interceptor;
import com.absir.server.on.OnPut;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RouteEntry {

    protected List<Interceptor> interceptors;

    private List<RouteEntry> routeEntries;

    private List<RouteMethod> beforeMethods;

    private List<RouteMethod> afterMethods;

    private List<RouteException> routeExceptions;

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

    public static OnPut intercept(Input input, RouteEntry routeEntry) throws Throwable {
        if (routeEntry == null) {
            return invoke(input, routeEntry);

        } else {
            List<Interceptor> interceptors = routeEntry.interceptors;
            return routeEntry.intercept(interceptors == null ? null : interceptors.iterator(), input);
        }
    }

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

            input.doAfterInvoker();
            dispatcher.resolveReturnedValue(routeBean, onPut);

        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }

            onPut.setReturnThrowable(e);
            if (e.getClass() == ServerException.class) {
                if (((ServerException) e).getServerStatus() == ServerStatus.ON_SUCCESS) {
                    onPut.setReturnedFixed(true);
                    input.doAfterInvoker();
                    dispatcher.resolveReturnedValue(routeBean, onPut);
                    return onPut;
                }
            }

            if (!dispatcher.returnThrowable(e, routeBean, onPut)) {
                throw e;
            }

        } finally {
            OnPut.close();
            input.close();
        }

        return onPut;
    }

    public static void invoke(Object routeBean, OnPut onPut, RouteEntry routeEntry) throws Throwable {
        if (routeEntry == null) {
            RouteAction routeAction = onPut.getInput().getRouteAction();
            if (routeAction != null) {
                routeAction.getRouteMethod().invoke(routeBean, onPut, false);
            }

        } else {
            List<RouteMethod> routeMethods = routeEntry.beforeMethods;
            if (routeMethods != null) {
                for (RouteMethod routeMethod : routeMethods) {
                    routeMethod.invoke(routeBean, onPut, false);
                }
            }

            invoke(routeBean, onPut, null);
            routeMethods = routeEntry.afterMethods;
            if (routeMethods != null) {
                for (RouteMethod routeMethod : routeMethods) {
                    routeMethod.invoke(routeBean, onPut, false);
                }
            }
        }
    }

    public List<RouteEntry> getRouteEntries() {
        return routeEntries;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public List<RouteMethod> getBeforeMethods() {
        return beforeMethods;
    }

    public List<RouteMethod> getAfterMethods() {
        return afterMethods;
    }

    public List<RouteException> getRouteExceptions() {
        return routeExceptions;
    }

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

    protected void addRouteEntries(RouteEntry routeEntry) {
        if (routeEntries == null) {
            routeEntries = new ArrayList<RouteEntry>();

        } else if (routeEntries.contains(routeEntry)) {
            return;
        }

        routeEntries.add(routeEntry);
    }

    protected void addInterceptor(Interceptor interceptor) {
        if (interceptors == null) {
            interceptors = new ArrayList<Interceptor>();

        } else if (interceptors.contains(interceptor)) {
            return;
        }

        interceptors.add(interceptor);
    }

    protected void addBeforeMethod(RouteMethod routeMethod) {
        if (beforeMethods == null) {
            beforeMethods = new ArrayList<RouteMethod>();
        }

        beforeMethods.add(routeMethod);
    }

    protected void addAfterMethod(RouteMethod routeMethod) {
        if (afterMethods == null) {
            afterMethods = new ArrayList<RouteMethod>();
        }

        afterMethods.add(routeMethod);
    }

    protected void addRouteException(RouteException routeException) {
        if (routeExceptions == null) {
            routeExceptions = new ArrayList<RouteException>();
        }

        routeExceptions.add(routeException);
    }

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

    public OnPut intercept(Iterator<Interceptor> iterator, Input input) throws Throwable {
        if (iterator == null || !iterator.hasNext()) {
            return invoke(input, this);

        } else {
            return iterator.next().intercept(iterator, input);
        }
    }
}
