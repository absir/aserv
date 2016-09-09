package com.absir.client.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/9.
 */
public abstract class RpcAdapter implements IRpcAdapter {

    protected Map<Class, Object> clsMapRpcInvoker = new HashMap<Class, Object>();

    public <T> T getRpcInvoker(Class<T> interfaceClass, boolean cacheProxyClass) {
        Object invoker = clsMapRpcInvoker.get(interfaceClass);
        if (invoker == null) {
            synchronized (this) {
                invoker = clsMapRpcInvoker.get(interfaceClass);
                if (invoker == null) {
                    invoker = RpcFactory.createRpcInvoker(this, interfaceClass, cacheProxyClass);
                    clsMapRpcInvoker.put(interfaceClass, invoker);
                }
            }
        }

        return (T) invoker;
    }

}
