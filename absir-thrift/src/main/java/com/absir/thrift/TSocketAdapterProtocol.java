package com.absir.thrift;

import com.absir.client.SocketAdapter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.protocol.TMessage;

/**
 * Created by absir on 2016/12/20.
 */
public class TSocketAdapterProtocol extends TAdapterProtocol<SocketAdapter> {

    public TSocketAdapterProtocol(TAdapterTransport<SocketAdapter> adapterTransport, String serviceName) {
        super(adapterTransport, serviceName);
    }

    @Override
    protected void sendMessage(TMessage message, ByteArrayOutputStream outputStream) {
        String uri = TSocketAdapterReceiver.getServiceUri(serviceName, message.name);
        boolean recv = message.type == 1;
        if (recv) {
            atomBuffer_ = new UtilAtomBuffer();
        }

        final UtilAtomBuffer atomBuffer = atomBuffer_;
        final int seqid = message.seqid;
        getTransport().getAdapter().sendDataVarints(uri, encrypt(outputStream), getTimeout(message), recv ? new SocketAdapter.CallbackAdapter() {
            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                atomBuffer.seqid = seqid;
                atomBuffer.offset = offset;
                atomBuffer.buffer = buffer;
                atomBuffer.decrement();
            }

        } : null);
    }
}
