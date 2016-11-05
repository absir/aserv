package com.absir.client.rpc;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.data.helper.HelperDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

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
            returns = new Object[1];
            atom = new UtilAtom();
            atom.increment();
        }

        SocketAdapter.CallbackAdapter callbackAdapter = async ? null : new SocketAdapterSel.CallbackAdapterStream() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                try {
                    int code = buffer == null ? RpcFactory.RPC_CODE.RPC_ERROR.ordinal() : SocketAdapter.getVarints(buffer, offset, buffer.length);
                    if (code == RpcFactory.RPC_CODE.RPC_SUCCESS.ordinal()) {
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

                } finally {
                    atom.decrement();
                }
            }

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer, InputStream inputStream) {
                if (inputStream == null) {
                    doWith(adapter, offset, buffer);

                } else {
                    try {
                        if (returnType == InputStream.class) {
                            // 直接返回流
                            returns[0] = inputStream;

                        } else {
                            try {
                                if (returnType != null || returnType != void.class) {
                                    // 流转换成对象返回
                                    returns[0] = HelperDataFormat.PACK.read(inputStream, returnType);
                                }

                            } catch (Throwable e) {
                                LOGGER.error("rpc error uri = " + uri, e);
                                returns[0] = RpcFactory.RPC_CODE.RETRUN_ERROR;
                            }
                        }

                    } finally {
                        atom.decrement();
                    }
                }
            }
        };

        if (attribute == null || !attribute.sendStream) {
            socketAdapter.sendDataIndexVarints(uri, paramData, timeout, callbackAdapter);

        } else {
            final PipedOutputStream outputStream = new PipedOutputStream();
            boolean start = false;
            try {
                UtilContext.getThreadPoolExecutor().execute(new Runnable() {
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
                });
                start = true;

            } finally {
                if (!start) {
                    UtilPipedStream.closeCloseable(outputStream);
                }
            }

            PipedInputStream inputStream = new PipedInputStream();
            inputStream.connect(outputStream);
            socketAdapter.sendStream(uri.getBytes(), true, false, inputStream, outputStream, timeout, callbackAdapter);
        }

        if (async) {
            return null;
        }

        atom.await();
        return returns[0];
    }
}
