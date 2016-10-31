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

    private static Map<Class<?>, HandlerType> clsMapHandlerType;

    protected Class<T> type;

    protected Map<String, HandlerMethod> handlerMethodMap;

    protected HandlerType() {
    }

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
                    handlerType = create(type, server, null);
                    clsMapHandlerType.put(type, handlerType);
                }
            }
        }

        return handlerType;
    }

    public static HandlerMethod createHandlerMethod(Method method) {
        HandlerMethod handlerMethod = new HandlerMethod();
        handlerMethod.method = method;
        handlerMethod.parameterTypes = KernelLang.getOptimizeClasses(method.getParameterTypes());
        handlerMethod.exceptionTypes = KernelLang.getOptimizeClasses(method.getExceptionTypes());
        return handlerMethod;
    }

    public static <T extends IHandler> HandlerType<T> create(Class<T> type, boolean server, Map<Method, ?> methodMapAction) {
        Map<String, HandlerMethod> handlerMethodMap = new HashMap<String, HandlerMethod>();
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (name.charAt(0) == '_' || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || method.getAnnotation(Close.class) != null) {
                continue;
            }

            if (methodMapAction != null && methodMapAction.containsKey(method)) {
                continue;
            }

            if (!server && method.getAnnotation(Server.class) == null) {
                continue;
            }

            int count = method.getParameterCount();
            if (count > 0) {
                name += ':' + count;
            }

            if (handlerMethodMap.containsKey(name)) {
                throw new RuntimeException("HandlerType[" + type + "] has conflict method name = " + name);
            }

            HandlerMethod handlerMethod = createHandlerMethod(method);
            handlerMethodMap.put(name, handlerMethod);
        }

        HandlerType handlerType = new HandlerType();
        handlerType.type = type;
        handlerType.handlerMethodMap = KernelLang.getOptimizeMap(handlerMethodMap);
        return handlerType;
    }

    public Map<String, HandlerMethod> getHandlerMethodMap() {
        return handlerMethodMap;
    }

    public static class HandlerMethod {

        protected Method method;

        protected Class<?>[] parameterTypes;

        protected Class<?>[] exceptionTypes;
    }

}
