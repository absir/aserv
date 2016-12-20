package com.absir.thrift;

import com.absir.client.SocketAdapter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.InputStream;

/**
 * Created by absir on 2016/12/20.
 */
public class TAdapterTransport extends TIOStreamTransport {

    private SocketAdapter socketAdapter;

    public SocketAdapter getSocketAdapter() {
        return socketAdapter;
    }

    public TAdapterTransport(SocketAdapter adapter) {
        super(new ByteArrayOutputStream());
        socketAdapter = adapter;
    }

    public ByteArrayOutputStream getOutputStream() {
        return (ByteArrayOutputStream) outputStream_;
    }


    public void setInputStream(InputStream inputStream) {
        inputStream_ = inputStream;
    }

    @Override
    public void flush() throws TTransportException {
    }
}
