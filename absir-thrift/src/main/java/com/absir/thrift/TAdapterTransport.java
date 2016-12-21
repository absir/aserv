package com.absir.thrift;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.InputStream;

/**
 * Created by absir on 2016/12/20.
 */
public class TAdapterTransport<T> extends TIOStreamTransport {

    private T adapter;

    public TAdapterTransport(T adapter) {
        super(new ByteArrayOutputStream());
        this.adapter = adapter;
    }

    public T getAdapter() {
        return adapter;
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
