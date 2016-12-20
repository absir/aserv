package com.absir.thrift;

import com.absir.client.SocketAdapter;
import com.absir.core.util.UtilAtom;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;

/**
 * Created by absir on 2016/12/20.
 */
public class TAdapterProtocol extends TCompactProtocol {

    public String serviceName;

    @Override
    public TAdapterTransport getTransport() {
        return (TAdapterTransport) super.getTransport();
    }

    public String getServiceName() {
        return serviceName;
    }

    public TAdapterProtocol(TAdapterTransport adapterTransport, String serviceName) {
        super(adapterTransport);
        this.serviceName = serviceName;
    }

    protected TMessage message_;

    protected static class UtilAtomBuffer extends UtilAtom {

        public int seqid;

        public int offset;

        public byte[] buffer;

    }

    private UtilAtomBuffer atomBuffer_;

    @Override
    public void writeMessageBegin(TMessage message) throws TException {
        message_ = message;
        getTransport().getOutputStream().reset();
        atomBuffer_ = null;
    }

    public int getTimeout(TMessage message) {
        return 30000;
    }

    @Override
    public void writeMessageEnd() throws TException {
        ByteArrayOutputStream byteArrayOutputStream = getTransport().getOutputStream();
        try {
            if (message_ != null) {
                TMessage message = message_;
                message_ = null;
                String uri = serviceName + ":" + message.name;
                boolean recv = message.type == 1;
                if (recv) {
                    atomBuffer_ = new UtilAtomBuffer();
                }

                final UtilAtomBuffer atomBuffer = atomBuffer_;
                final int seqid = message.seqid;
                getTransport().getSocketAdapter().sendDataVarints(uri, byteArrayOutputStream.toByteArray(), getTimeout(message), recv ? new SocketAdapter.CallbackAdapter() {
                    @Override
                    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                        atomBuffer.seqid = seqid;
                        atomBuffer.offset = offset;
                        atomBuffer.buffer = buffer;
                        atomBuffer.decrement();
                    }

                } : null);
            }

        } finally {
            byteArrayOutputStream.reset();
        }
    }

    protected static final byte[] UNKOWN_EXCEPTION_BYTES;

    static {
        TApplicationException applicationException = new TApplicationException();
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        try {
            applicationException.write(new TCompactProtocol(new TIOStreamTransport(outputStream)));

        } catch (TException e) {
            e.printStackTrace();
        }

        UNKOWN_EXCEPTION_BYTES = outputStream.toByteArray();
    }

    @Override
    public TMessage readMessageBegin() throws TException {
        if (atomBuffer_ != null) {
            atomBuffer_.increment();
            atomBuffer_.await();
            int seqid = atomBuffer_.seqid;
            int offset = atomBuffer_.offset;
            byte[] buffer = atomBuffer_.buffer;
            atomBuffer_ = null;
            TMessage message;
            if (buffer == null || offset >= buffer.length) {
                message = new TMessage("", (byte) 3, seqid);
                getTransport().setInputStream(new ByteArrayInputStream(UNKOWN_EXCEPTION_BYTES));

            } else {
                byte type = buffer[offset++];
                message = new TMessage("", type, seqid);
                if (type == 3 && offset >= buffer.length) {
                    getTransport().setInputStream(new ByteArrayInputStream(UNKOWN_EXCEPTION_BYTES));

                } else {
                    getTransport().setInputStream(new ByteArrayInputStream(buffer, offset, buffer.length));
                }
            }

            return message;
        }

        return super.readMessageBegin();
    }

    @Override
    public void readMessageEnd() throws TException {
        getTransport().setInputStream(null);
    }

}
