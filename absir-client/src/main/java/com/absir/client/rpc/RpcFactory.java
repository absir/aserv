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

    public enum RPC_CODE implements IRpcCode {

        RUN_ERROR,

        RUN_SUCCESS,

        SEND_ERROR,

        NO_NAME,

        NO_PERMISSION,

        NO_METHOD,

        PARAM_ERROR,

        RUN_EXCEPTION,;

        static Map<Integer, IRpcCode> eiMapRpcCode;

        public static final int codeForException(int ei) {
            return ei + RUN_EXCEPTION.ordinal() + 1;
        }

        public static final IRpcCode rpcCodeForException(int ei) {
            if (eiMapRpcCode == null) {
                synchronized (RPC_CODE.class) {
                    if (eiMapRpcCode == null) {
                        eiMapRpcCode = new HashMap<Integer, IRpcCode>();
                    }
                }
            }

            Integer key = ei;
            IRpcCode rpcCode = eiMapRpcCode.get(key);
            if (rpcCode == null) {
                synchronized (eiMapRpcCode) {
                    rpcCode = eiMapRpcCode.get(key);
                    if (rpcCode == null) {
                        if (ei < 0) {
                            ei = 0;
                            key = ei;
                        }

                        rpcCode = new RpcCode(codeForException(ei));
                        eiMapRpcCode.put(key, rpcCode);
                    }
                }
            }

            return rpcCode;
        }
    }

    public static interface IRpcCode {

        public int ordinal();

    }

    protected static class RpcCode implements IRpcCode {

        protected int code;

        public RpcCode(int code) {
            this.code = code;
        }

        @Override
        public int ordinal() {
            return code;
        }
    }

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

            Object value = rpcAdapter.sendDataIndexVarints(rpcMethod.attribute, rpcMethod.uri, args);
            Class<?> cls = value == null ? null : value.getClass();
            if (cls != RPC_CODE.class && cls != RpcCode.class) {
                return value;
            }

            IRpcCode rpcCode = (IRpcCode) value;
            if (cls == RPC_CODE.class) {
                throw new RpcException(rpcCode.ordinal());
            }

            int code = rpcCode.ordinal() - RPC_CODE.RUN_EXCEPTION.ordinal() - 1;
            Class<?>[] exceptionTypes = rpcMethod.exceptionTypes;
            if (exceptionTypes != null && code >= 0 && code < exceptionTypes.length) {
                throw (Throwable) KernelClass.newInstance(exceptionTypes[code]);
            }

            throw new RpcException(RPC_CODE.RUN_EXCEPTION.ordinal() + 1);
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
