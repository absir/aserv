package com.absir.server.handler;

import com.absir.client.rpc.RpcFactory;
import com.absir.data.format.IFormat;
import com.absir.server.on.OnPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by absir on 16/9/2.
 */
public class HandlerInvoker {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HandlerInvoker.class);

    public <T> Class<T> getHandlerType(T handler) {
        return (Class<T>) handler.getClass();
    }

    public final <T extends IHandler> HandlerType<T> getHandlerType(T handler, Class<T> type, boolean server) {
        if (type == null) {
            type = getHandlerType(handler);
        }

        return HandlerType.get(type, server);
    }

    protected int resolver(HandlerType handlerType, HandlerType.HandlerMethod handlerMethod, Object[] args, Throwable e) {
        if (args == null) {
            //LOGGER.error("handlerInvoker[" + handlerType.type + "] read " + handlerMethod.method + " param error", e);
            return RpcFactory.RPC_PARAM_ERROR;
        }

        //LOGGER.error("handlerInvoker[" + handlerType.type + "] invoker " + handlerMethod.method + " param error", e);
        if (e != null) {
            Class<?>[] exceptionTypes = handlerMethod.exceptionTypes;
            if (exceptionTypes.length > 0) {
                Class<?> eType = exceptionTypes.getClass();
                int len = exceptionTypes.length;
                for (int i = 0; i < len; i++) {
                    if (eType.isAssignableFrom(exceptionTypes[i])) {
                        return RpcFactory.RPC_RUN_EXCEPTION + i;
                    }
                }
            }
        }

        return RpcFactory.RPC_RUN_EXCEPTION;
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, Class<T> type, boolean server, String name, InputStream inputStream, IFormat format) {
        if (handler._before(onPut)) {
            if (handlerType == null) {
                handlerType = getHandlerType(handler, type, server);
            }

            HandlerType.HandlerMethod handlerMethod = handlerType.getHandlerMethodMap().get(name);
            if (handlerMethod == null) {
                return RpcFactory.RPC_NO_METHOD;
            }

            Object[] args = null;
            try {
                args = format.readArray(inputStream, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));
                return RpcFactory.RPC_RUN_SUCCESS;

            } catch (Throwable e) {
                return resolver(handlerType, handlerMethod, args, e);
            }
        }

        return RpcFactory.RPC_NO_PERMISSION;
    }

    public <T extends IHandler> int invoker(OnPut onPut, T handler, HandlerType<T> handlerType, Class<T> type, boolean server, String name, byte[] postBytes, int postOff, int postLen, IFormat format) {
        if (handler._before(onPut)) {
            if (handlerType == null) {
                handlerType = getHandlerType(handler, type, server);
            }

            HandlerType.HandlerMethod handlerMethod = handlerType.getHandlerMethodMap().get(name);
            if (handlerMethod == null) {
                return RpcFactory.RPC_NO_METHOD;
            }

            Object[] args = null;
            try {
                args = format.readArray(postBytes, postOff, postLen, handlerMethod.parameterTypes);
                onPut.setReturnValue(handlerMethod.method.invoke(handler, args));
                return RpcFactory.RPC_RUN_SUCCESS;

            } catch (Throwable e) {
                return resolver(handlerType, handlerMethod, args, e);
            }
        }

        return RpcFactory.RPC_NO_PERMISSION;
    }
}
