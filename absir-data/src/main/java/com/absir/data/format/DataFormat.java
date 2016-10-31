package com.absir.data.format;

import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelLang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by absir on 16/9/2.
 */
public abstract class DataFormat implements IFormat {

    public void write(OutputStream outputStream, Object object) throws IOException {
        if (object == null) {
            return;
        }

        Class<?> cls = object.getClass();
        if (cls == byte[].class) {
            outputStream.write((byte[]) object);

        } else if (cls == String.class) {
            outputStream.write(((String) object).getBytes(KernelCharset.getDefault()));

        } else {
            formatWrite(outputStream, object);
        }
    }

    public void writeArray(OutputStream outputStream, Object... objects) throws IOException {
        if (objects != null) {
            formatWriteArray(outputStream, objects);
        }
    }

    public byte[] writeAsBytes(Object object) throws IOException {
        if (object == null) {
            return KernelLang.NULL_BYTES;
        }

        if (object.getClass() == byte[].class) {
            return (byte[]) object;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(outputStream, object);
        return outputStream.toByteArray();
    }

    public byte[] writeAsBytesArray(Object... objects) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeArray(outputStream, objects);
        return outputStream.toByteArray();
    }

    public String writeAsStringArray(Object... objects) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeArray(outputStream, objects);
        return outputStream.toString(KernelCharset.getDefault().name());
    }

    public <T> T read(InputStream inputStream, Class<T> toClass) throws IOException {
        return (T) read(inputStream, (Type) toClass);
    }

    public Object read(InputStream inputStream, Type toType) throws IOException {
        if (toType == null || toType == Void.class || inputStream.available() == 0) {
            return null;
        }

        if (toType == byte[].class) {
            return HelperIO.toByteArray(inputStream);

        } else if (toType == String.class) {
            return HelperIO.toString(inputStream, KernelCharset.getDefault());
        }

        return formatRead(inputStream, toType);
    }

    public Object[] readArray(InputStream inputStream, Type... toTypes) throws IOException {
        if (toTypes.length == 0) {
            return KernelLang.NULL_OBJECTS;
        }

        return formatReadArray(inputStream, toTypes);
    }

    public Object read(byte[] bytes, Type toType) throws IOException {
        if (bytes == null) {
            return null;
        }

        return read(bytes, 0, bytes.length, toType);
    }

    public Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
        if (toType == null || toType == Void.class || off >= len) {
            return null;
        }

        if (toType == byte[].class) {
            return bytes;

        } else if (toType == String.class) {
            return new String(bytes, off, len, KernelCharset.getDefault());
        }

        return formatRead(bytes, off, len, toType);
    }

    public Object[] readArray(byte[] bytes, Type... toTypes) throws IOException {
        return readArray(bytes, 0, bytes.length, toTypes);
    }

    public Object[] readArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException {
        if (toTypes.length == 0) {
            return KernelLang.NULL_OBJECTS;
        }

        if (len <= 0) {
            len = bytes.length;
        }

        return formatReadArray(bytes, off, len, toTypes);
    }

    public Object[] readArray(String string, Type... toTypes) throws IOException {
        if (toTypes.length == 0) {
            return KernelLang.NULL_OBJECTS;
        }

        return readArray(string.getBytes(KernelCharset.getDefault()), toTypes);
    }

    protected abstract void formatWrite(OutputStream outputStream, Object object) throws IOException;

    protected abstract void formatWriteArray(OutputStream outputStream, Object... objects) throws IOException;

    protected abstract Object formatRead(InputStream inputStream, Type toType) throws IOException;

    protected abstract Object[] formatReadArray(InputStream inputStream, Type... toTypes) throws IOException;

    protected abstract Object formatRead(byte[] bytes, int off, int len, Type toType) throws IOException;

    protected abstract Object[] formatReadArray(byte[] bytes, int off, int len, Type... toTypes) throws IOException;
}
