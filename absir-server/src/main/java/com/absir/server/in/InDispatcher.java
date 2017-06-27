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
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.util.UtilAbsir;
import com.absir.data.format.IFormat;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@Configure
public abstract class InDispatcher<T, R> implements IDispatcher<T> {

    @Inject
    static HandlerInvoker handlerInvoker;

    @Inject
    private static RouteAdapter routeAdapter;

    @Inject
    private static HandlerAdapter handlerAdapter;

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
        if (uri == null || !Environment.isActive()) {
            return false;
        }

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
        if (routeMatcher.getParameterLength() == 0) {
            input.writeUriDict();
        }

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

    // Handler处理入口
    public boolean onHandler(String uri, T req, R res) throws IOException {
        if (uri.length() > 1) {
            HandlerAdapter.HandlerAction handlerAction = handlerAdapter.on(uri);
            if (handlerAction != null) {
                Input input = input(uri, getInMethod(req), new InModel(), req, res);
                input.writeUriDict();
                OnPut onPut = onPut(input, handlerAction.handler);
                try {
                    onPut.open();
                    InputStream inputStream = input.getInputStream();
                    int code = handlerInvoker.invoker(onPut, handlerAction.handler, handlerAction.handlerProxy, handlerAction.handlerType, handlerAction.handlerMethod, inputStream);
                    resolverHandler(onPut, input, code);

                } finally {
                    onPut.close();
                }

                return true;
            }
        }

        return false;
    }

    // Handler返回处理
    public void resolverHandler(OnPut onPut, Input input, int code) throws IOException {
        Object returned = onPut.getReturned();
        if (returned != null && returned instanceof IFormat) {
            IFormat format = (IFormat) returned;
            if (input.setCode(code)) {
                OutputStream outputStream = input.getOutputStream();
                if (outputStream == null) {
                    input.write(format.writeAsBytes(onPut.getReturnValue()));

                } else {
                    format.write(outputStream, onPut.getReturnValue());
                }

            } else {
                OutputStream outputStream = input.getOutputStream();
                ByteArrayOutputStream byteArrayOutputStream = null;
                if (outputStream == null) {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    outputStream = byteArrayOutputStream;
                }

                outputStream.write(KernelByte.getVarintsLengthBytes(code));
                format.write(outputStream, onPut.getReturnValue());
                if (byteArrayOutputStream != null) {
                    input.write(byteArrayOutputStream.toByteArray());
                }
            }
        }
    }
}
