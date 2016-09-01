package com.absir.client.rpc;

import com.absir.client.value.Rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/1.
 */
public class RpcInterface {

    //protected Class<?> interfaceClass;

    protected Map<String, RpcMethod> rpcMethodMap;

    public static class RpcAttribute {

        protected long timeout;

    }

    public static class RpcMethod {

        protected RpcAttribute attribute;

        protected Class<?> returnType;

        //protected Class<?>[] parameterTypes;

        protected Class<?>[] exceptionTypes;

        protected String uri;

    }

    protected RpcInterface() {

    }

    private static Map<Class<?>, RpcInterface> clsMapRpcInterface;

    public static RpcInterface ReadInterface(Class<?> interfaceClass) {
        if (clsMapRpcInterface == null) {
            synchronized (RpcInterface.class) {
                if (clsMapRpcInterface == null) {
                    clsMapRpcInterface = new HashMap<Class<?>, RpcInterface>();
                }
            }
        }

        RpcInterface rpcInterface = clsMapRpcInterface.get(interfaceClass);
        if (rpcInterface == null) {
            synchronized (clsMapRpcInterface) {
                rpcInterface = clsMapRpcInterface.get(interfaceClass);
                if (rpcInterface == null) {
                    if (!interfaceClass.isInterface()) {
                        throw new RuntimeException("RpcInterface[" + interfaceClass + "] must be interface");
                    }

                    RpcAttribute attribute = getRpcAttributeClass(interfaceClass);
                    Map<String, RpcMethod> rpcMethodMap = new HashMap<String, RpcMethod>();
                    for (Method method : interfaceClass.getMethods()) {
                        String name = method.getName();
                        if (rpcMethodMap.containsKey(name)) {
                            throw new RuntimeException("RpcInterface[" + interfaceClass + "] has conflict method name = " + name);
                        }

                        RpcMethod rpcMethod = new RpcMethod();
                        rpcMethod.attribute = getRpcAttributeMethod(attribute, method);
                        rpcMethod.returnType = method.getReturnType();
//                        rpcMethod.parameterTypes = method.getParameterTypes();
//                        if(rpcMethod.parameterTypes.length == 0) {
//                            rpcMethod.parameterTypes = null;
//                        }

                        rpcMethod.exceptionTypes = method.getExceptionTypes();
                        if (rpcMethod.exceptionTypes.length == 0) {
                            rpcMethod.exceptionTypes = null;
                        }

                        rpcMethod.uri = "Rpc:" + interfaceClass.getName() + ":" + name;
                        rpcMethodMap.put(name, rpcMethod);
                    }

                    rpcInterface = new RpcInterface();
                    //rpcInterface.interfaceClass = interfaceClass;
                    rpcInterface.rpcMethodMap = rpcMethodMap;
                    clsMapRpcInterface.put(interfaceClass, rpcInterface);
                }
            }
        }

        return rpcInterface;
    }

    public static RpcAttribute getRpcAttributeClass(Class<?> interfaceClass) {
        Rpc rpc = interfaceClass.getAnnotation(Rpc.class);
        if (rpc != null) {
            RpcAttribute attribute = new RpcAttribute();
            attribute.timeout = rpc.timeout();
        }

        return null;
    }

    public static RpcAttribute getRpcAttributeMethod(RpcAttribute rpcAttribute, Method method) {
        Rpc rpc = method.getAnnotation(Rpc.class);
        if (rpc != null) {
            RpcAttribute attribute = new RpcAttribute();
            attribute.timeout = rpc.timeout();
            rpcAttribute = attribute;
        }

        return rpcAttribute;
    }
}
