package com.absir.client.rpc;

import com.absir.client.value.Rpc;
import com.absir.client.value.RpcName;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/1.
 */
public class RpcInterface {

    //protected Class<?> interfaceType;

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

    public Map<String, RpcMethod> getRpcMethodMap() {
        return rpcMethodMap;
    }

    private static Map<Class<?>, RpcInterface> clsMapRpcInterface;

    protected static void remove(Class<?> type) {
        if (clsMapRpcInterface != null) {
            clsMapRpcInterface.remove(type);
        }
    }

    public static RpcInterface get(Class<?> interfaceType) {
        if (clsMapRpcInterface == null) {
            synchronized (RpcInterface.class) {
                if (clsMapRpcInterface == null) {
                    clsMapRpcInterface = new HashMap<Class<?>, RpcInterface>();
                }
            }
        }

        RpcInterface rpcInterface = clsMapRpcInterface.get(interfaceType);
        if (rpcInterface == null) {
            synchronized (clsMapRpcInterface) {
                rpcInterface = clsMapRpcInterface.get(interfaceType);
                if (rpcInterface == null) {
                    if (!interfaceType.isInterface()) {
                        throw new RuntimeException("RpcInterface[" + interfaceType + "] must be interface");
                    }

                    String rpcName = getRpcName(interfaceType);
                    RpcAttribute attribute = getRpcAttributeClass(interfaceType);
                    Map<String, RpcMethod> rpcMethodMap = new HashMap<String, RpcMethod>();
                    for (Method method : interfaceType.getMethods()) {
                        String name = method.getName();
                        if (rpcMethodMap.containsKey(name)) {
                            throw new RuntimeException("RpcInterface[" + interfaceType + "] has conflict method name = " + name);
                        }

                        RpcMethod rpcMethod = new RpcMethod();
                        rpcMethod.attribute = getRpcAttributeMethod(attribute, method);
                        rpcMethod.returnType = method.getReturnType();
//                        rpcMethod.parameterTypes = method.getParameterTypes();
//                        if(rpcMethod.parameterTypes.length == 0) {
//                            rpcMethod.parameterTypes = null;
//                        }

                        rpcMethod.exceptionTypes = KernelLang.getOptimizeClasses(method.getExceptionTypes());
                        rpcMethod.uri = "_rpc/" + rpcName + '/' + name;
                        rpcMethodMap.put(name, rpcMethod);
                    }

                    rpcInterface = new RpcInterface();
                    //rpcInterface.interfaceType = interfaceType;
                    rpcInterface.rpcMethodMap = rpcMethodMap;
                    clsMapRpcInterface.put(interfaceType, rpcInterface);
                }
            }
        }

        return rpcInterface;
    }

    public static String getRpcName(Class<?> interfaceType) {
        RpcName rpcName = interfaceType.getAnnotation(RpcName.class);
        String name = rpcName.value();
        return KernelString.isEmpty(name) ? interfaceType.getName().replace('$', '.') : name;
    }

    public static RpcAttribute getRpcAttributeClass(Class<?> interfaceType) {
        Rpc rpc = interfaceType.getAnnotation(Rpc.class);
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
