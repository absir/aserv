package com.absir.thrift;

import com.absir.core.util.UtilAtom;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by absir on 2016/12/20.
 */
public abstract class TAdapterProtocol<T> extends TCompactProtocol {

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

    protected String serviceName;
    protected TMessage message_;
    protected UtilAtomBuffer atomBuffer_;

    public TAdapterProtocol(TAdapterTransport<T> adapterTransport, String serviceName) {
        super(adapterTransport);
        this.serviceName = serviceName;
    }

    @Override
    public TAdapterTransport<T> getTransport() {
        return (TAdapterTransport) super.getTransport();
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void writeMessageBegin(TMessage message) throws TException {
        message_ = message;
        getTransport().getOutputStream().reset();
        atomBuffer_ = null;
    }

    public int getTimeout(TMessage message) {
        return 30000;
    }

    public byte[] encrypt(ByteArrayOutputStream outputStream) {
        return outputStream.toByteArray();
    }

    public InputStream decrypt(int offset, byte[] buffer) {
        return new ByteArrayInputStream(buffer, offset, buffer.length);
    }

    @Override
    public void writeMessageEnd() throws TException {
        ByteArrayOutputStream byteArrayOutputStream = getTransport().getOutputStream();
        try {
            if (message_ != null) {
                TMessage message = message_;
                message_ = null;
                sendMessage(message, byteArrayOutputStream);
            }

        } finally {
            byteArrayOutputStream.reset();
        }
    }

    protected abstract void sendMessage(TMessage message, ByteArrayOutputStream outputStream);

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
                    getTransport().setInputStream(decrypt(offset, buffer));
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

    protected static class UtilAtomBuffer extends UtilAtom {

        public int seqid;

        public int offset;

        public byte[] buffer;

    }

}
