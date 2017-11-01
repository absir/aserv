package com.absir.client.rpc;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelByte;
import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.data.helper.HelperDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/10/28.
 */
public class RpcSocketAdapter<T extends SocketAdapter> extends RpcAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RpcSocketAdapter.class);

    protected T socketAdapter;

    public RpcSocketAdapter(T adapter) {
        socketAdapter = adapter;
    }

    public T getSocketAdapter() {
        return socketAdapter;
    }

    protected void setSocketAdapter(T socketAdapter) {
        this.socketAdapter = socketAdapter;
    }

    @Override
    public byte[] paramData(RpcInterface.RpcAttribute attribute, Object[] args) throws IOException {
        return attribute == null || !attribute.sendStream ? HelperDataFormat.PACK.writeAsBytesArray(args) : null;
    }

    public int getDefaultTimeout() {
        return 30000;
    }

    @Override
    public Object sendDataIndexVarints(final RpcInterface.RpcAttribute attribute, final String uri, byte[] paramData, final Object[] args, final Class<?>[] parameterTypes, final Class<?> returnType) throws IOException {
        int timeout = attribute == null ? 0 : attribute.timeout;
        if (timeout == 0) {
            timeout = getDefaultTimeout();
        }

        boolean async = attribute != null && attribute.async;
        final Object[] returns;
        final UtilAtom atom;
        if (async) {
            returns = null;
            atom = null;

        } else {
            returns = new Object[2];
            atom = new UtilAtom();
            atom.increment();
        }

        SocketAdapter.CallbackAdapter callbackAdapter = async ? null : new SocketAdapterSel.CallbackAdapterStream() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                try {
                    returns[0] = buffer;
                    returns[1] = offset;

                } finally {
                    atom.decrement();
                }
            }

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer, final InputStream inputStream) {
                if (inputStream == null) {
                    doWith(adapter, offset, buffer);

                } else {
                    try {
                        returns[0] = inputStream;
                        returns[1] = InputStream.class;

                    } finally {
                        atom.decrement();
                    }
                }
            }
        };

        socketAdapter.clearRetryConnect();
        if (attribute == null || !attribute.sendStream) {
            socketAdapter.sendDataVarints(uri, paramData, timeout, callbackAdapter);

        } else {
            if (UtilContext.isWarnIdlePool()) {
                return RpcFactory.RPC_CODE.NO_THREAD;
            }

            UtilPipedStream.OutInputStream inputStream = new UtilPipedStream.OutInputStream();
            final UtilPipedStream.WrapOutStream outputStream = new UtilPipedStream.WrapOutStream(inputStream);
            Runnable inputRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        HelperDataFormat.PACK.writeArrayInputStream(outputStream, parameterTypes, args);

                    } catch (Exception e) {
                        Environment.throwable(e);

                    } finally {
                        UtilPipedStream.closeCloseable(outputStream);
                    }
                }
            };

            socketAdapter.sendStream(uri.getBytes(), true, false, inputStream, outputStream, timeout, callbackAdapter, attribute.sendInputStream ? inputRunnable : null);
            if (!attribute.sendInputStream) {
                inputRunnable.run();
            }
        }

        if (atom == null) {
            return null;
        }

        atom.await();
        if (returns[1] == InputStream.class) {
            if (returnType != InputStream.class && returnType != null && returnType != void.class) {
                InputStream inputStream = (InputStream) returns[0];
                try {
                    int code = KernelByte.getVarintsLength(inputStream);
                    if (code == RpcFactory.RPC_CODE.RPC_SUCCESS.ordinal()) {
                        returns[0] = HelperDataFormat.PACK.read(inputStream, returnType);

                    } else {
                        returns[0] = RpcFactory.RPC_CODE.rpcCodeForException(code);
                    }

                } finally {
                    UtilPipedStream.closeCloseable(inputStream);
                }
            }

        } else {
            byte[] buffer = (byte[]) returns[0];
            int offset = (Integer) returns[1];
            int code = buffer == null ? RpcFactory.RPC_CODE.RPC_ERROR.ordinal() : SocketAdapter.getVarints(buffer, offset, buffer.length);
            if (attribute != null && attribute.rpcData >= 0 && !attribute.sendStream) {
                //rpcData resolver
                if (code != RpcFactory.RPC_CODE.RPC_SUCCESS.ordinal()) {
                    RpcData rpcData = new RpcData();
                    rpcData.args = args;
                    rpcData.uri = uri;
                    rpcData.paramData = paramData;
                    resolverRpcData(attribute, rpcData);
                }

                if (returnType != null || returnType != void.class) {
                    returns[0] = DynaBinder.to(null, returnType);
                }

            } else if (code == RpcFactory.RPC_CODE.RPC_SUCCESS.ordinal()) {
                offset += KernelByte.getVarintsLength(code);
                try {
                    if (returnType != null || returnType != void.class) {
                        returns[0] = HelperDataFormat.PACK.read(buffer, offset, buffer.length, returnType);
                    }

                } catch (Throwable e) {
                    LOGGER.error("rpc error uri = " + uri, e);
                    returns[0] = RpcFactory.RPC_CODE.RETRUN_ERROR;
                }

            } else {
                returns[0] = RpcFactory.RPC_CODE.rpcCodeForException(code);
            }
        }

        return returns[0];
    }

    protected void resolverRpcData(RpcInterface.RpcAttribute attribute, RpcData rpcData) {
    }

}
