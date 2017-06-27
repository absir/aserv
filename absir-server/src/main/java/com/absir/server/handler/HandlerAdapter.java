package com.absir.server.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.inject.InjectBeanUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.client.rpc.RpcInterface;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.server.value.Close;
import com.absir.server.value.Handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/5.
 */
@Base
@Bean
public class HandlerAdapter {

    protected Map<String, HandlerAction> handlerActionMap;

    public HandlerAction on(String uri) {
        return handlerActionMap == null ? null : handlerActionMap.get(uri);
    }

    @Inject
    public void addHandlers(IHandler[] handlerBeans) {
        Map<String, HandlerAction> map = handlerActionMap == null ? new HashMap<String, HandlerAction>() : new HashMap<String, HandlerAction>(handlerActionMap);
        for (IHandler handlerBean : handlerBeans) {
            Class<? extends IHandler> beanType = (Class<? extends IHandler>) InjectBeanUtils.getBeanType(handlerBean);
            if (BeanConfigImpl.getTypeAnnotation(beanType, Close.class) != null) {
                continue;
            }

            Handler handler = BeanConfigImpl.getTypeAnnotation(beanType, Handler.class);
            if (handler == null) {
                continue;
            }

            addHandlerBeanType(map, handlerBean, handler, beanType);
        }

        handlerActionMap = map.isEmpty() ? null : map;
    }

    protected void addHandlerBeanType(Map<String, HandlerAction> map, IHandler handlerBean, Handler handler, Class<? extends IHandler> beanType) {
        IHandlerProxy handlerProxy = null;
        if (handler instanceof IHandlerProxy) {
            handlerProxy = (IHandlerProxy) handler;
            beanType = handlerProxy.getInterface();
        }

        Map<Method, HandlerAction> methodMapAction = new HashMap<Method, HandlerAction>();
        for (Class<?> rpcType : beanType.getInterfaces()) {
            String rpcName = RpcInterface.getRpcName(null, rpcType);
            if (rpcName != null) {
                for (Method method : rpcType.getMethods()) {
                    method = KernelReflect.realMethod(beanType, method);
                    String uri = RpcInterface.getRpcUri(rpcName, method);
                    if (map.containsKey(uri)) {
                        throw new RuntimeException("HandlerAdapter[" + beanType + "] has conflict method uri = " + uri);
                    }

                    HandlerAction action = new HandlerAction();
                    action.handler = handlerBean;
                    action.handlerProxy = handlerProxy;
                    action.handlerMethod = HandlerType.createHandlerMethod(method);
                    map.put(uri, action);
                    methodMapAction.put(method, action);
                }
            }
        }

        HandlerType<?> handlerType = HandlerType.create(beanType, handler != null && handler.value(), methodMapAction);
        for (HandlerAction action : methodMapAction.values()) {
            action.handlerType = handlerType;
        }

        if (handlerType.handlerMethodMap != null && !handlerType.handlerMethodMap.isEmpty()) {
            IHandlerRoute route = handler instanceof IHandlerRoute ? (IHandlerRoute) handler : null;
            String rpcName = RpcInterface.getRpcName(null, beanType);
            if (KernelString.isEmpty(rpcName)) {
                rpcName = beanType.getSimpleName();
            }

            for (HandlerType.HandlerMethod handlerMethod : handlerType.handlerMethodMap.values()) {
                String uri = route == null ? RpcInterface.getRpcUri(rpcName, handlerMethod.method) : route.getHandlerUri(rpcName, handlerMethod.method);
                if (map.containsKey(uri)) {
                    throw new RuntimeException("HandlerAdapter[" + beanType + "] has conflict method uri = " + uri);
                }

                HandlerAction action = new HandlerAction();
                action.handler = handlerBean;
                action.handlerProxy = handlerProxy;
                action.handlerType = handlerType;
                action.handlerMethod = handlerMethod;
                map.put(uri, action);
            }
        }
    }

    public static class HandlerAction {

        public IHandler handler;

        public IHandlerProxy handlerProxy;

        public HandlerType handlerType;

        public HandlerType.HandlerMethod handlerMethod;
    }

}
