/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年6月10日 下午7:28:42
 */
package com.absir.client.callback;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel.CallbackAdapteStream;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelClass;
import com.absir.core.util.UtilFuture;
import com.absir.data.helper.HelperDatabind;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
public abstract class CallbackMsg<T> implements CallbackAdapteStream {

	/** TYPE_VARIABLE */
	public static final TypeVariable<?> TYPE_VARIABLE = CallbackMsg.class.getTypeParameters()[0];

	/** beanType */
	protected Type beanType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.client.SocketAdapter.CallbackAdapte#doWith(com.absir.client.
	 * SocketAdapter, int, byte[])
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.client.SocketAdapterSel.CallbackAdapteStream#doWith(com.absir.
	 * client.SocketAdapter, int, byte[], java.io.InputStream)
	 */
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

	/**
	 * @param bytes
	 * @param off
	 * @param len
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	protected Object read(byte[] bytes, int off, int len, Type toType) throws IOException {
		return HelperDatabind.read(bytes, off, len, toType);
	}

	/**
	 * @param inputStream
	 * @param toType
	 * @return
	 * @throws IOException
	 */
	protected Object read(InputStream inputStream, Type toType) throws IOException {
		return HelperDatabind.read(inputStream, toType);
	}

	/**
	 * @param bean
	 * @param ok
	 * @param buffer
	 * @param adapter
	 */
	public abstract void doWithBean(T bean, boolean ok, byte[] buffer, SocketAdapter adapter);

	/**
	 * @author absir
	 *
	 * @param <T>
	 */
	public static class CallbackJsonFuture<T> extends CallbackMsg<T> {

		/** future */
		private UtilFuture<T> future = new UtilFuture<T>();

		/**
		 * @return the future
		 */
		public UtilFuture<T> getFuture() {
			return future;
		}

		/**
		 * @return
		 */
		public T getFutureBean() {
			return future.getBean();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.system.converter.BodyMsgPack.CallbackMessagePack
		 * #doWithBean(java.lang.Object, boolean, byte[],
		 * com.absir.aserv.system.adapter.SocketAdapter)
		 */
		@Override
		public void doWithBean(T bean, boolean ok, byte[] buffer, SocketAdapter adapter) {
			future.setBean(bean);
		}
	}
}
