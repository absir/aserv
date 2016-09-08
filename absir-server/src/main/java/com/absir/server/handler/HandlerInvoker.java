package com.absir.server.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.InjectBeanUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.client.rpc.RpcFactory;
import com.absir.data.format.IFormat;
import com.absir.data.helper.HelperDataFormat;
import com.absir.server.on.OnPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by absir on 16/9/2.
 */
@Base
@Bean
public class HandlerInvoker {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HandlerInvoker.class);

    public final <T extends IHandler> HandlerType<T> getHandlerType(T handler, Class<T> type, boolean server) {
        if (type == null) {
            type = (Class<T>) InjectBeanUtils.getBeanType(handler);
        }

        return HandlerType.get(type, server);
    }

    public IFormat getFormat(OnPut onPut, IHandler handler, HandlerType.HandlerMethod handlerMethod) {
        return onPut.getInput().isDebug() ? HelperDataFormat.JSON : HelperDataFormat.PACK;
    }

    protected int throwable(OnPut onPut, HandlerType handlerType, HandlerType.HandlerMethod handlerMethod, Object[] args, Throwable e) {
        if (args == null) {
            //LOGGER.error("handlerInvoker[" + handlerType.type + "] read " + handlerMethod.method + " param error", e);
            return RpcFactory.RPC_CODE.PARAM_ERROR.ordinal();
        }

        //LOGGER.error("handlerInvoker[" + handlerType.type + "] invoker " + handlerMethod.method + " param error", e);
        if (e != null) {
            onPut.setReturnThrowable(e);
            Class<?>[] exceptionTypes = handlerMethod.exceptionTypes;
            if (exceptionTypes.length > 0) {
                Class<?> eType = exceptionTypes.getClass();
                int len = exceptionTypes.length;
                for (int i = 0; i < len; i++) {
                    if (eType.isAssignableFrom(exceptionTypes[i])) {
                        return RpcFactory.RPC_CODE.codeForException(i);
                    }
                }
            }
        }

        return RpcFactory.RPC_CODE.RUN_EXCEPTION.ordinal();
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, Class<T> type, boolean server, String name, InputStream inputStream) {
        if (handler._permission(onPut)) {
            if (handlerType == null) {
                handlerType = getHandlerType(handler, type, server);
            }

            HandlerType.HandlerMethod handlerMethod = handlerType.getHandlerMethodMap().get(name);
            if (handlerMethod == null) {
                return RpcFactory.RPC_CODE.NO_METHOD.ordinal();
            }

            IFormat format = getFormat(onPut, handler, handlerMethod);
            onPut.setReturned(format);
            Object[] args = null;
            try {
                args = format.readArray(inputStream, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));

            } catch (Throwable e) {
                return throwable(onPut, handlerType, handlerMethod, args, e);
            }

            handler._finally(onPut, handlerMethod);
            return RpcFactory.RPC_CODE.RUN_SUCCESS.ordinal();
        }

        return RpcFactory.RPC_CODE.NO_PERMISSION.ordinal();
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, Class<T> type, boolean server, String name, byte[] postBytes, int postOff, int postLen) {
        if (handler._permission(onPut)) {
            if (handlerType == null) {
                handlerType = getHandlerType(handler, type, server);
            }

            HandlerType.HandlerMethod handlerMethod = handlerType.getHandlerMethodMap().get(name);
            if (handlerMethod == null) {
                return RpcFactory.RPC_CODE.NO_METHOD.ordinal();
            }

            IFormat format = getFormat(onPut, handler, handlerMethod);
            onPut.setReturned(format);
            Object[] args = null;
            try {
                args = format.readArray(postBytes, postOff, postLen, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));

            } catch (Throwable e) {
                return throwable(onPut, handlerType, handlerMethod, args, e);
            }

            handler._finally(onPut, handlerMethod);
            return RpcFactory.RPC_CODE.RUN_SUCCESS.ordinal();
        }

        return RpcFactory.RPC_CODE.NO_PERMISSION.ordinal();
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, HandlerType.HandlerMethod handlerMethod, InputStream inputStream) {
        if (handler._permission(onPut)) {
            IFormat format = getFormat(onPut, handler, handlerMethod);
            onPut.setReturned(format);
            Object[] args = null;
            try {
                args = format.readArray(inputStream, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));

            } catch (Throwable e) {
                return throwable(onPut, handlerType, handlerMethod, args, e);
            }

            return RpcFactory.RPC_CODE.RUN_SUCCESS.ordinal();
        }

        handler._finally(onPut, handlerMethod);
        return RpcFactory.RPC_CODE.NO_PERMISSION.ordinal();
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, HandlerType.HandlerMethod handlerMethod, byte[] postBytes, int postOff, int postLen) {
        if (handler._permission(onPut)) {
            IFormat format = getFormat(onPut, handler, handlerMethod);
            onPut.setReturned(format);
            Object[] args = null;
            try {
                args = format.readArray(postBytes, postOff, postLen, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));

            } catch (Throwable e) {
                return throwable(onPut, handlerType, handlerMethod, args, e);
            }

            handler._finally(onPut, handlerMethod);
            return RpcFactory.RPC_CODE.RUN_SUCCESS.ordinal();
        }

        return RpcFactory.RPC_CODE.NO_PERMISSION.ordinal();
    }
}
