package com.absir.data.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by absir on 16/9/2.
 */
public class DataFormatWrap extends DataFormat {

    private IFormat format;

    public DataFormatWrap(IFormat format) {
        this.format = format;
    }

    @Override
    protected void formatWrite(OutputStream outputStream, Object object) throws IOException {
        format.write(outputStream, object);
    }

    @Override
    protected void formatWriteArray(OutputStream outputStream, Class<?>[] types, Object... objects) throws IOException {
        format.writeArray(outputStream, types, objects);
    }

    @Override
    protected Object formatRead(InputStream inputStream, Type toType) throws IOException {
        return format.read(inputStream, toType);
    }

    @Override
    protected Object[] formatReadArray(InputStream inputStream, Type... toTypes) throws IOException {
        return format.readArray(inputStream, toTypes);
    }

    @Override
    protected Object formatRead(byte[] bytes, int off, int len, Type toType) throws IOException {
        return format.read(bytes, off, len, toType);
    }

    @Override
    protected Object[] formatReadArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException {
        return format.readArray(bytes, off, len, toTypes);
    }
}
