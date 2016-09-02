package com.absir.server.handler;

import com.absir.core.kernel.KernelLang;
import com.absir.server.value.Close;
import com.absir.server.value.Server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/2.
 */
public class HandlerType<T> {

    protected Class<T> type;

    protected Map<String, HandlerMethod> handlerMethodMap;

    public static class HandlerMethod {

        protected Method method;

        protected Class<?>[] parameterTypes;

        protected Class<?>[] exceptionTypes;
    }

    protected HandlerType() {
    }

    public Map<String, HandlerMethod> getHandlerMethodMap() {
        return handlerMethodMap;
    }

    private static Map<Class<?>, HandlerType> clsMapHandlerType;

    protected static void remove(Class<? extends IHandler> type) {
        if (clsMapHandlerType != null) {
            clsMapHandlerType.remove(type);
        }
    }

    public static <T extends IHandler> HandlerType<T> get(Class<T> type, boolean server) {
        if (clsMapHandlerType == null) {
            synchronized (HandlerType.class) {
                if (clsMapHandlerType == null) {
                    clsMapHandlerType = new HashMap<Class<?>, HandlerType>();
                }
            }
        }

        HandlerType handlerType = clsMapHandlerType.get(type);
        if (handlerType == null) {
            synchronized (clsMapHandlerType) {
                handlerType = clsMapHandlerType.get(type);
                if (handlerType == null) {
                    handlerType = create(type, server);
                    clsMapHandlerType.put(type, handlerType);
                }
            }
        }

        return handlerType;
    }

    public static <T extends IHandler> HandlerType<T> create(Class<T> type, boolean server) {
        Map<String, HandlerMethod> handlerMethodMap = new HashMap<String, HandlerMethod>();
        for (Method method : type.getMethods()) {
            String name = method.getName();
            if (name.charAt(0) == '_' || Modifier.isStatic(method.getModifiers()) || method.getAnnotation(Close.class) != null) {
                continue;
            }

            if (!server && method.getAnnotation(Server.class) == null) {
                continue;
            }

            if (handlerMethodMap.containsKey(name)) {
                throw new RuntimeException("HandlerType[" + type + "] has conflict method name = " + name);
            }

            HandlerMethod handlerMethod = new HandlerMethod();
            handlerMethod.method = method;
            handlerMethod.parameterTypes = KernelLang.getOptimizeClasses(method.getParameterTypes());
            handlerMethod.exceptionTypes = KernelLang.getOptimizeClasses(method.getExceptionTypes());
            handlerMethodMap.put(name, handlerMethod);
        }

        HandlerType handlerType = new HandlerType();
        handlerType.type = type;
        handlerType.handlerMethodMap = handlerMethodMap;
        return handlerType;
    }

}
