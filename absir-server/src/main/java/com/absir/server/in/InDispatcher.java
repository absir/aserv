/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-30 下午7:12:07
 */
package com.absir.server.in;

import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Inject;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteAdapter;
import com.absir.server.route.RouteEntry;
import com.absir.server.route.RouteException;
import com.absir.server.route.RouteMatcher;
import com.absir.server.route.returned.ReturnedResolver;

import java.util.List;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Configure
public abstract class InDispatcher<T, R> implements IDispatcher<T> {

    @Inject
    private static RouteAdapter routeAdapter;

    /**
     * @return the routeAdapter
     */
    public static RouteAdapter getRouteAdapter() {
        return routeAdapter;
    }

    /**
     * @param uri
     * @param req
     * @param res
     * @return
     * @throws Exception
     */
    public boolean on(String uri, T req, R res) throws Throwable {
        Object[] routes = routeAdapter.route(uri, this, req);
        if (routes != null) {
            try {
                return on(input(routes.length < 4 ? uri : (String) routes[3], (InMethod) routes[1], (InModel) routes[2], req, res), (RouteMatcher) routes[0]);

            } catch (Throwable e) {
                if (e instanceof ServerException) {
                    ServerStatus servStatus = ((ServerException) e).getServerStatus();
                    switch (servStatus) {
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

    /**
     * @param input
     * @param routeMatcher
     * @return
     * @throws Throwable
     */
    public boolean on(Input input, RouteMatcher routeMatcher) throws Throwable {
        input.setDispatcher(this);
        input.setRouteMatcher(routeMatcher);
        return RouteEntry.intercept(input, input.getRouteEntry()) != null;
    }

    /**
     * @param uri
     * @param inMethod
     * @param model
     * @param req
     * @param res
     * @return
     */
    protected abstract Input input(String uri, InMethod inMethod, InModel model, T req, R res);

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.in.IDispatcher#onPut(com.absir.server.in.Input,
     * java.lang.Object)
     */
    @Override
    public OnPut onPut(Input input, Object routeBean) {
        return new OnPut(input);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.server.in.IDispatcher#returnThrowable(java.lang.Throwable,
     * java.lang.Object, com.absir.server.on.OnPut)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.server.in.IDispatcher#resolveReturnedValue(java.lang.Object,
     * com.absir.server.on.OnPut)
     */
    @Override
    public void resolveReturnedValue(Object routeBean, OnPut onPut) throws Throwable {
        ReturnedResolver returnedResolver = onPut.getReturnedResolver();
        if (returnedResolver == null) {
            returnedResolver = onPut.getInput().getReturnedResolver(onPut);
            if (returnedResolver == null) {
                return;
            }
        }

        returnedResolver.resolveReturnedValue(onPut.getReturnValue(), onPut.getReturned(), onPut);
    }
}
