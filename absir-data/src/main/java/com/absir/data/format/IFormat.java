package com.absir.data.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by absir on 16/9/2.
 */
public interface IFormat {

    public void write(OutputStream outputStream, Object object) throws IOException;

    public void writeArray(OutputStream outputStream, Object... objects) throws IOException;

    public Object read(InputStream inputStream, Type toType) throws IOException;

    public Object[] readArray(InputStream inputStream, Type... toTypes) throws IOException;

    public Object read(byte[] bytes, int off, int len, Type toType) throws IOException;

    public Object[] readArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException;
}
