package com.absir.client.rpc;

import com.absir.client.SocketAdapter;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDatabind;
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

    public static int RPC_DO_ERROR = 0;

    public static int RPC_DO_SUCCESS = 1;

    public static int RPC_SEND_ERROR = 2;

    public static int RPC_NO_METHOD = 3;

    public static int RPC_PARAM_ERROR = 4;

    public static int RPC_DO_EXCEPTION = 5;

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

    public static class RpcReturn {

        public int code;

        public Object value;
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
                throw new Exception("rpcInvoker[" + rpcType + "] not found method = " + method);
            }

            long timeout = rpcMethod.attribute == null ? rpcAdapter.getDefaultTimeout() : rpcMethod.attribute.timeout;
            if (timeout < 10) {
                timeout = 10;
            }

            byte[] postBytes = length == 0 ? null : HelperDatabind.PACK.writeAsBytesArray(args);

            final RpcReturn rpcReturn = new RpcReturn();
            final UtilAtom atom = new UtilAtom();
            atom.increment();
            rpcAdapter.sendDataIndexVarints(rpcMethod.uri, postBytes, timeout, new SocketAdapter.CallbackAdapter() {
                @Override
                public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                    try {
                        if (adapter == null) {
                            rpcReturn.code = 1;
                        }

                        int length = buffer == null ? 0 : buffer.length;
                        int code = rpcReturn.code = SocketAdapter.getVarints(buffer, offset, length);
                        if (code == 1) {
                            rpcReturn.value = HelperDatabind.PACK.read(buffer, offset + SocketAdapter.getVarintsLength(code), length, rpcMethod.returnType);
                        }

                    } catch (Throwable e) {
                        LOGGER.error("rpc invoker error method = " + method, e);

                    } finally {
                        atom.decrement();
                    }
                }
            });

            atom.await();
            int code = rpcReturn.code;
            if (code == 1) {
                return rpcReturn.value;
            }

            if (code <= 5) {
                throw new RpcException(code);
            }

            code -= 5;
            Class<?>[] exceptionTypes = rpcMethod.exceptionTypes;
            if (exceptionTypes != null && code < exceptionTypes.length) {
                throw (Throwable) KernelClass.newInstance(exceptionTypes[code]);
            }

            throw new RpcException(6);
        }
    }

    protected Map<Class<?>, Class<?>> rpcClsMapProxyClass;

    public <T> T createRpcInvoker(IRpcAdapter rpcAdapter, Class<T> interfaceClass, boolean cacheProxyClass) {
        RpcInterface rpcInterface = RpcInterface.ReadInterface(interfaceClass);
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
