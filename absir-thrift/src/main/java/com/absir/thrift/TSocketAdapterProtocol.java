package com.absir.thrift;

import com.absir.client.SocketAdapter;
import com.absir.core.base.Environment;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.protocol.TMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/12/20.
 */
public class TSocketAdapterProtocol extends TAdapterProtocol<TSocketAdapter> {

    public TSocketAdapterProtocol(TAdapterTransport<TSocketAdapter> adapterTransport, String serviceName) {
        super(adapterTransport, serviceName);
    }

    @Override
    public byte[] encrypt(byte[] buffer) throws IOException {
        return getTransport().getAdapter().encrypt(buffer);
    }

    @Override
    public InputStream decrypt(int offset, byte[] buffer) throws IOException {
        return getTransport().getAdapter().decrypt(offset, buffer);
    }

    @Override
    protected void sendMessage(TMessage message, ByteArrayOutputStream outputStream) {
        boolean recv = message.type == 1;
        if (recv) {
            atomBuffer_ = new UtilAtomBuffer();
        }

        final UtilAtomBuffer atomBuffer = atomBuffer_;
        final int seqid = message.seqid;
        // 准备SocketAdapter回调
        SocketAdapter.CallbackAdapter callbackAdapter = recv ? new SocketAdapter.CallbackAdapter() {
            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                atomBuffer.seqid = seqid;
                atomBuffer.offset = offset;
                atomBuffer.buffer = buffer;
                atomBuffer.decrement();
            }

        } : null;

        try {
            sendMessageCallback(message, outputStream, callbackAdapter);
            message = null;

        } finally {
            if (atomBuffer_ != null && message != null) {
                atomBuffer_.decrement();
            }
        }
    }

    protected void sendMessageCallback(TMessage message, ByteArrayOutputStream outputStream, SocketAdapter.CallbackAdapter callbackAdapter) {
        final String uri = TSocketAdapterReceiver.getServiceUri(serviceName, message.name);
        SocketAdapter socketAdapter = getTransport().getAdapter().getSocketAdapter();
        // 发送AdapterDataBytes
        final byte[] postDataBytes = outputStream.toByteArray();
        final int callbackIndex = socketAdapter.getNextCallbackIndex(callbackAdapter);
        socketAdapter.clearRetryConnect();
        socketAdapter.sendAdapterDataBytes(callbackIndex, new SocketAdapter.AdapterDataBytes() {
            @Override
            public byte[] getSendDataBytes(SocketAdapter socketAdapter) {
                try {
                    byte[] postBytes = encrypt(postDataBytes);
                    return socketAdapter.sendDataBytesVarintsReal(0, uri, false, (byte) 0, callbackIndex, postBytes, 0, postBytes.length);

                } catch (IOException e) {
                    Environment.throwable(e);
                }

                return null;

            }

        }, getTimeout(message), callbackAdapter);
    }
}
