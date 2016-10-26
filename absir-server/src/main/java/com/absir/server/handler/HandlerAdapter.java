package com.absir.server.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.inject.InjectBeanUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.client.rpc.RpcInterface;
import com.absir.core.kernel.KernelClass;
import com.absir.server.value.Close;
import com.absir.server.value.Handler;

import java.lang.annotation.Annotation;
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
                Annotation annotation = KernelClass.fetchAnnotations(beanType, Close.class, Handler.class);
                if (annotation != null) {
                    if (annotation instanceof Close) {
                        continue;
                    }

                    handler = (Handler) annotation;
                }
            }

            HandlerType<?> handlerType = HandlerType.create(beanType, handler != null && handler.value());
            if (handlerType.handlerMethodMap == null || handlerType.handlerMethodMap.isEmpty()) {
                continue;
            }

            String name = RpcInterface.getRpcName(beanType);
            for (Map.Entry<String, HandlerType.HandlerMethod> entry : handlerType.handlerMethodMap.entrySet()) {
                String handlerName = '_' + name + entry.getKey();
                if (map.containsKey(handlerName)) {
                    throw new RuntimeException("HandlerAdapter[" + beanType + "] has conflict method name = " + handlerName);
                }

                HandlerAction action = new HandlerAction();
                action.handler = handlerBean;
                action.handlerType = handlerType;
                action.handlerMethod = entry.getValue();
                map.put(handlerName, action);
            }
        }

        handlerActionMap = map.isEmpty() ? null : map;
    }

    public static class HandlerAction {

        public IHandler handler;

        public HandlerType handlerType;

        public HandlerType.HandlerMethod handlerMethod;
    }

}
