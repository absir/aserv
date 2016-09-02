package com.absir.client.rpc;

import com.absir.core.kernel.KernelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/9/1.
 */
public class RpcFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RpcFactory.class);

    public static int RPC_RUN_ERROR = 0;

    public static int RPC_RUN_SUCCESS = 1;

    public static int RPC_SEND_ERROR = 2;

    public static int RPC_NO_NAME = 3;

    public static int RPC_NO_PERMISSION = 4;

    public static int RPC_NO_METHOD = 5;

    public static int RPC_PARAM_ERROR = 6;

    // 执行未定义异常
    public static int RPC_RUN_EXCEPTION = 7;

    public static interface IRpcInvoker {

        public Class<?> getRpcType();

        public IRpcAdapter getRpcAdapter();

        public void setRpcAdapter(IRpcAdapter adapter);

        public RpcInterface getRpcInterface();
    }

    private static final Map<String, Integer> PROXY_METHOD_ZERO = new HashMap<String, Integer>();

    private static final Map<String, Integer> PROXY_METHOD_ONE = new HashMap<String, Integer>();

    static {
        PROXY_METHOD_ZERO.put("getRpcType", 0);
        PROXY_METHOD_ZERO.put("getRpcAdapter", 1);
        PROXY_METHOD_ZERO.put("getRpcInterface", 2);
        PROXY_METHOD_ZERO.put("hashCode", 3);
        PROXY_METHOD_ZERO.put("toString", 4);
        // AOP_PROXY_METHOD_ONE
        PROXY_METHOD_ONE.put("equals", 0);
        PROXY_METHOD_ONE.put("setRpcAdapter", 1);
    }

    public static class RpcException extends RuntimeException {

        public int code;

        public RpcException(int code) {
            this.code = code;
        }
    }

    public static class RpcInvoker implements InvocationHandler {

        protected Class<?> rpcType;

        protected IRpcAdapter rpcAdapter;

        protected RpcInterface rpcInterface;

        @Override
        public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
            int length = args == null ? 0 : args.length;
            if (length == 0) {
                Integer interceptor = PROXY_METHOD_ZERO.get(method.getName());
                if (interceptor != null) {
                    switch (interceptor) {
                        case 0:
                            return rpcType;

                        case 1:
                            return rpcAdapter;

                        case 2:
                            return rpcInterface;

                        case 3:
                            return hashCode();

                        case 4:
                            return toString();
                    }
                }

            } else if (length == 1) {
                Integer interceptor = PROXY_METHOD_ONE.get(method.getName());
                if (interceptor != null) {
                    switch (interceptor) {
                        case 0:
                            return this == args[0];
                        case 1:
                            rpcAdapter = (IRpcAdapter) args[0];
                            return null;
                    }
                }
            }

            final RpcInterface.RpcMethod rpcMethod = rpcInterface.rpcMethodMap.get(method.getName());
            if (rpcMethod == null) {
                throw new Exception("rpc[" + rpcType + "] not found method = " + method);
            }

            IRpcAdapter.RpcReturn rpcReturn = rpcAdapter.sendDataIndexVarints(rpcMethod.attribute, rpcMethod.uri, args);
            int code = rpcReturn.code;
            if (code == 1) {
                return rpcReturn.value;
            }

            if (code <= RPC_RUN_EXCEPTION) {
                throw new RpcException(code);
            }

            code -= RPC_RUN_EXCEPTION;
            Class<?>[] exceptionTypes = rpcMethod.exceptionTypes;
            if (exceptionTypes != null && code < exceptionTypes.length) {
                throw (Throwable) KernelClass.newInstance(exceptionTypes[code]);
            }

            throw new RpcException(RPC_RUN_EXCEPTION + 1);
        }
    }

    protected Map<Class<?>, Class<?>> rpcClsMapProxyClass;

    public <T> T createRpcInvoker(IRpcAdapter rpcAdapter, Class<T> interfaceClass, boolean cacheProxyClass) {
        RpcInterface rpcInterface = RpcInterface.get(interfaceClass);
        RpcInvoker rpcInvoker = new RpcInvoker();
        rpcInvoker.rpcType = interfaceClass;
        rpcInvoker.rpcAdapter = rpcAdapter;
        rpcInvoker.rpcInterface = rpcInterface;
        if (!cacheProxyClass) {
            return (T) Proxy.newProxyInstance(RpcFactory.class.getClassLoader(), new Class<?>[]{interfaceClass, IRpcInvoker.class}, rpcInvoker);
        }

        if (rpcClsMapProxyClass == null) {
            synchronized (RpcFactory.class) {
                if (rpcClsMapProxyClass == null) {
                    rpcClsMapProxyClass = new HashMap<Class<?>, Class<?>>();
                }
            }
        }

        Class<?> proxyClass = rpcClsMapProxyClass.get(interfaceClass);
        if (proxyClass == null) {
            synchronized (rpcClsMapProxyClass) {
                proxyClass = rpcClsMapProxyClass.get(interfaceClass);
                if (proxyClass == null) {
                    Object proxy = Proxy.newProxyInstance(RpcFactory.class.getClassLoader(), new Class<?>[]{interfaceClass, IRpcInvoker.class}, rpcInvoker);
                    rpcClsMapProxyClass.put(interfaceClass, proxy.getClass());
                    return (T) proxy;
                }
            }
        }

        Object proxy = KernelClass.declaredNew(proxyClass, rpcInvoker);
        return (T) (proxy == null ? Proxy.newProxyInstance(RpcFactory.class.getClassLoader(), new Class<?>[]{interfaceClass, IRpcInvoker.class}, rpcInvoker) : proxy);
    }

}
