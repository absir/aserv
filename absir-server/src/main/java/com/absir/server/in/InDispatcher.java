/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午7:12:07
 */
package com.absir.server.in;

import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Inject;
import com.absir.core.util.UtilAbsir;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.handler.HandlerAdapter;
import com.absir.server.handler.HandlerInvoker;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteAdapter;
import com.absir.server.route.RouteEntry;
import com.absir.server.route.RouteException;
import com.absir.server.route.RouteMatcher;
import com.absir.server.route.returned.ReturnedResolver;

import java.io.InputStream;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@Configure
public abstract class InDispatcher<T, R> implements IDispatcher<T> {

    @Inject
    private static RouteAdapter routeAdapter;

    @Inject
    private static HandlerAdapter handlerAdapter;

    @Inject
    static HandlerInvoker handlerInvoker;

    public static RouteAdapter getRouteAdapter() {
        return routeAdapter;
    }

    public static HandlerAdapter getHandlerAdapter() {
        return handlerAdapter;
    }

    public static HandlerInvoker getHandlerInvoker() {
        return handlerInvoker;
    }

    public boolean on(String uri, T req, R res) throws Throwable {
        if (onHandler(uri, req, res)) {
            return true;
        }

        Object[] routes = routeAdapter.route(uri, this, req);
        if (routes != null) {
            try {
                return on(input(routes.length < 4 ? uri : (String) routes[3], (InMethod) routes[1], (InModel) routes[2], req, res), (RouteMatcher) routes[0]);

            } catch (Throwable e) {
                Throwable throwable = UtilAbsir.forCauseThrowable(e);
                if (throwable instanceof ServerException) {
                    ServerStatus serverStatus = ((ServerException) throwable).getServerStatus();
                    switch (serverStatus) {
                        case IN_404:
                            return false;

                        case ON_SUCCESS:
                            return true;

                        default:
                            break;
                    }
                }

                throw e;
            }
        }

        return false;
    }

    public boolean on(Input input, RouteMatcher routeMatcher) throws Throwable {
        input.setDispatcher(this);
        input.setRouteMatcher(routeMatcher);
        return RouteEntry.intercept(input, input.getRouteEntry()) != null;
    }

    protected abstract Input input(String uri, InMethod inMethod, InModel model, T req, R res);

    @Override
    public OnPut onPut(Input input, Object routeBean) {
        return new OnPut(input);
    }

    @Override
    public boolean returnThrowable(Throwable e, Object routeBean, OnPut onPut) throws Throwable {
        RouteEntry routeEntry = onPut.getInput().getRouteEntry();
        if (routeEntry != null) {
            List<RouteException> routeExceptions = routeEntry.getRouteExceptions();
            if (routeExceptions != null) {
                for (RouteException routeException : routeExceptions) {
                    if (routeException.invoke(e, routeBean, onPut)) {
                        resolveReturnedValue(routeBean, onPut);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable {
        ReturnedResolver returnedResolver = onPut.getReturnedResolver();
        if (returnedResolver == null) {
            if (onPut.isReturnedFixed()) {
                return;
            }

            returnedResolver = onPut.getInput().getReturnedResolver(onPut);
            if (returnedResolver == null) {
                return;
            }
        }

        returnedResolver.resolveReturnedValue(onPut.getReturnValue(), onPut.getReturned(), onPut);
    }

    public boolean onHandler(String uri, T req, R res) {
        if (uri.length() > 1 && uri.startsWith("_")) {
            HandlerAdapter.HandlerAction handlerAction = handlerAdapter.on(uri);
            if (handlerAction != null) {
                Input input = input(uri, getInMethod(req), new InModel(), req, res);
                OnPut onPut = onPut(input, handlerAction.handler);
                try {
                    onPut.open();
                    InputStream inputStream = input.getInputStream();
                    handlerInvoker.invoker(onPut, handlerAction.handler, handlerAction.handlerType, handlerAction.handlerMethod, inputStream);

                } catch (Throwable e) {

                } finally {
                    OnPut.close();
                }

                return true;
            }
        }

        return false;
    }
}
