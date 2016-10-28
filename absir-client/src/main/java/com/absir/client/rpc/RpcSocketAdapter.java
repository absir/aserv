package com.absir.client.rpc;

import com.absir.client.SocketAdapter;
import com.absir.core.kernel.KernelByte;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by absir on 2016/10/28.
 */
public class RpcSocketAdapter extends RpcAdapter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RpcSocketAdapter.class);

    protected SocketAdapter socketAdapter;

    public RpcSocketAdapter(SocketAdapter adapter) {
        socketAdapter = adapter;
    }

    public SocketAdapter getSocketAdapter() {
        return socketAdapter;
    }

    protected void setSocketAdapter(SocketAdapter socketAdapter) {
        this.socketAdapter = socketAdapter;
    }

    @Override
    public byte[] paramData(RpcInterface.RpcAttribute attribute, Object[] args) throws IOException {
        return HelperDataFormat.PACK.writeAsBytesArray(args);
    }

    @Override
    public Object sendDataIndexVarints(RpcInterface.RpcAttribute attribute, final String uri, byte[] paramData, final Class<?> returnType) {
        final UtilAtom atom = new UtilAtom();
        atom.increment();
        final Object[] returns = new Object[1];
        socketAdapter.sendDataIndexVarints(uri, paramData, attribute.timeout, new SocketAdapter.CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                try {
                    int code = KernelByte.getVarintsLength(buffer, offset);
                    if (code == RpcFactory.RPC_CODE.RUN_SUCCESS.ordinal()) {
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
        });

        atom.await();
        return returns[0];
    }
}
