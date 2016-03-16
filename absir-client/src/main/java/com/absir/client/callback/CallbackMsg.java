/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年6月10日 下午7:28:42
 */
package com.absir.client.callback;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel.CallbackAdapteStream;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilFuture;
import com.absir.data.helper.HelperDatabind;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

@SuppressWarnings("unchecked")
public abstract class CallbackMsg<T> implements CallbackAdapteStream {

    public static final TypeVariable<?> TYPE_VARIABLE = CallbackMsg.class.getTypeParameters()[0];

    protected Type beanType;

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
        if (buffer == null) {
            doWithBean(null, false, null, adapter);
            return;
        }

        if (buffer.length == 0) {
            doWithBean(null, true, null, adapter);
            return;
        }

        boolean ok = offset < 1 ? false : (buffer[0] & SocketAdapter.ERROR_FLAG) == 0;
        if (beanType == null) {
            beanType = KernelClass.type(getClass(), TYPE_VARIABLE);
            if (beanType == null) {
                beanType = CallbackMsg.class;
            }
        }

        T bean = null;
        if (ok && beanType != CallbackMsg.class) {
            int length = buffer.length;
            if (length > 5) {
                try {
                    if (beanType == byte[].class) {
                        bean = (T) buffer;

                    } else if (beanType == String.class) {
                        bean = (T) new String(buffer, offset, length - offset, KernelCharset.UTF8);

                    } else {
                        bean = (T) read(buffer, offset, length - offset, beanType);
                    }

                } catch (Exception e) {
                    if (Environment.getEnvironment() == Environment.DEVELOP) {
                        e.printStackTrace();
                    }
                }
            }
        }

        doWithBean(bean, ok, buffer, adapter);
    }

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer, InputStream inputStream) {
        boolean ok = offset < 1 ? false : (buffer[0] & SocketAdapter.ERROR_FLAG) == 0;
        if (beanType == null) {
            beanType = KernelClass.type(getClass(), TYPE_VARIABLE);
            if (beanType == null) {
                beanType = CallbackMsg.class;
            }
        }

        T bean = null;
        if (ok && beanType != CallbackMsg.class) {
            int length = buffer.length;
            if (length > 5) {
                try {
                    if (beanType == InputStream.class) {
                        bean = (T) inputStream;

                    } else if (beanType == String.class) {
                        bean = (T) HelperIO.toString(inputStream);

                    } else {
                        bean = (T) read(inputStream, beanType);
                    }

                } catch (Exception e) {
                    if (Environment.getEnvironment() == Environment.DEVELOP) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (beanType == InputStream.class) {
            bean = (T) inputStream;
        }

        doWithBean(bean, ok, buffer, adapter);
    }

    protected Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
        return HelperDatabind.read(bytes, off, len, toType);
    }

    protected Object read(InputStream inputStream, Type toType) throws IOException {
        return HelperDatabind.read(inputStream, toType);
    }

    public abstract void doWithBean(T bean, boolean ok, byte[] buffer, SocketAdapter adapter);

    public static class CallbackJsonFuture<T> extends CallbackMsg<T> {

        private UtilFuture<T> future = new UtilFuture<T>();

        public UtilFuture<T> getFuture() {
            return future;
        }

        public T getFutureBean() {
            return future.getBean();
        }

        @Override
        public void doWithBean(T bean, boolean ok, byte[] buffer, SocketAdapter adapter) {
            future.setBean(bean);
        }
    }
}
