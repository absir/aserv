package com.absir.client.rpc;

import com.absir.client.value.Rpc;
import com.absir.client.value.RpcRoute;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by absir on 16/9/1.
 */
public class RpcInterface {

    private static Map<Class<?>, RpcInterface> clsMapRpcInterface;
    protected Map<Method, RpcMethod> rpcMethodMap;

    protected RpcInterface() {
    }

    protected static void remove(Class<?> type) {
        if (clsMapRpcInterface != null) {
            clsMapRpcInterface.remove(type);
        }
    }

    public static String getRpcUri(String rpcName, Method method) {
        String name = method.getName();
        int count = method.getParameterCount();
        if (count == 0) {
            return "_r/" + rpcName + '/' + name;

        } else {
            return "_r/" + rpcName + '/' + name + ':' + count;
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

                    Rpc rpc = interfaceType.getAnnotation(Rpc.class);
                    if (rpc == null) {
                        throw new RuntimeException("RpcInterface[" + interfaceType + "] must has rpc annotation");
                    }

                    String rpcName = getRpcName(rpc, interfaceType);
                    RpcAttribute attribute = getRpcAttributeClass(rpc, interfaceType);
                    Set<String> uris = new HashSet<String>();
                    Map<Method, RpcMethod> rpcMethodMap = new HashMap<Method, RpcMethod>();
                    for (Method method : interfaceType.getMethods()) {
                        String uri = getRpcUri(rpcName, method);
                        if (!uris.add(uri)) {
                            throw new RuntimeException("RpcInterface[" + interfaceType + "] has conflict method uri = " + uri);
                        }

                        RpcMethod rpcMethod = new RpcMethod();
                        rpcMethod.uri = uri;
                        RpcAttribute mAttribute = getRpcAttributeMethod(attribute, method);
                        rpcMethod.attribute = mAttribute;
                        rpcMethod.returnType = method.getReturnType();
                        if (mAttribute != null && mAttribute.sendStream) {
                            // 发送流的时候才需要
                            rpcMethod.parameterTypes = method.getParameterTypes();
                        }

                        rpcMethod.exceptionTypes = KernelLang.getOptimizeClasses(method.getExceptionTypes());
                        rpcMethodMap.put(method, rpcMethod);
                    }

                    rpcInterface = new RpcInterface();
                    rpcInterface.rpcMethodMap = rpcMethodMap;
                    clsMapRpcInterface.put(interfaceType, rpcInterface);
                }
            }
        }

        return rpcInterface;
    }

    public static String getRpcName(Rpc rpc, Class<?> interfaceType) {
        if (rpc == null) {
            rpc = interfaceType.getAnnotation(Rpc.class);
            if (rpc == null) {
                return null;
            }
        }

        String name = rpc.name();
        if (!KernelString.isEmpty(name)) {
            return name;
        }

        RpcRoute rpcRoute = KernelClass.fetchAnnotation(interfaceType, RpcRoute.class);
        if (rpcRoute == null) {
            return interfaceType.getName().replace('$', '.');
        }

        name = interfaceType.getSimpleName();
        int pos = name.indexOf('_');
        if (pos > 0) {
            name = name.substring(pos);
        }

        String value = rpcRoute.value();
        if (!KernelString.isEmpty(value)) {
            name = value.charAt(value.length() - 1) == '/' ? (value + name) : (value + '/' + name);
        }

        return name;
    }

    public static boolean isCustomRpc(Rpc rpc) {
        return rpc.timeout() > 0 || rpc.sendStream() || rpc.returnStream();
    }

    public static RpcAttribute getRpcAttributeClass(Rpc rpc, Class<?> interfaceType) {
        if (rpc != null) {
            if (isCustomRpc(rpc)) {
                RpcAttribute attribute = new RpcAttribute();
                attribute.timeout = rpc.timeout();
                attribute.sendStream = rpc.sendStream();
                attribute.returnStream = rpc.returnStream();
                return attribute;
            }
        }

        return null;
    }

    public static RpcAttribute getRpcAttributeMethod(RpcAttribute rpcAttribute, Method method) {
        Rpc rpc = method.getAnnotation(Rpc.class);
        if (rpc != null) {
            int streamIndex = KernelArray.index(method.getParameterTypes(), InputStream.class);
            boolean returnStream = method.getReturnType() == InputStream.class;
            if (isCustomRpc(rpc) || streamIndex >= 0 || returnStream) {
                RpcAttribute attribute = new RpcAttribute();
                attribute.timeout = rpc == null ? 0 : rpc.timeout();
                attribute.sendStream = streamIndex >= 0 ? true : rpc == null ? false : rpc.sendStream();
                attribute.returnStream = returnStream ? true : rpc == null ? false : rpc.returnStream();
                rpcAttribute = attribute;
            }
        }

        return rpcAttribute;
    }

    public Map<Method, RpcMethod> getRpcMethodMap() {
        return rpcMethodMap;
    }

    public static class RpcAttribute {

        protected int timeout;

        protected boolean sendStream;

        protected boolean returnStream;

    }

    public static class RpcMethod {

        protected String uri;

        protected RpcAttribute attribute;

        protected Class<?> returnType;

        protected Class<?>[] parameterTypes;

        protected Class<?>[] exceptionTypes;

    }
}
