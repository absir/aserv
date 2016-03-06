/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-2-17 下午4:06:43
 */
package com.absir.server.socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.absir.bean.basis.Configure;
import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.SocketNIO;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilPipedStream;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.socket.resolver.SocketBufferResolver;

/**
 * @author absir
 * 
 */
@Configure
public abstract class InputSocket extends Input {

	/** NONE_RESPONSE */
	public static final String NONE_RESPONSE = "";

	/** NONE_RESPONSE_BYTES */
	public static final byte[] NONE_RESPONSE_BYTES = NONE_RESPONSE.getBytes();

	/** selSession */
	private SelSession selSession;

	/** socketChannel */
	private SocketChannel socketChannel;

	/** uri */
	private String uri;

	/** status */
	private int status = ServerStatus.ON_SUCCESS.getCode();

	/** flag */
	private byte flag;

	/** input */
	private int callbackIndex;

	/** inputStream */
	private byte[] inputBuffer;

	/** inputPos */
	private int inputPos;

	/** inputCount */
	private int inputCount;

	/** inputStream */
	private InputStream inputStream;

	/** input */
	private String input;

	/** outputStream */
	private OutputStream outputStream;

	/**
	 * @param model
	 * @param inputSocketAtt
	 * @param socketChannel
	 */
	public InputSocket(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
		super(model);
		this.socketChannel = socketChannel;
		setId(inputSocketAtt.getId());
		uri = inputSocketAtt.getUrl();
		flag = inputSocketAtt.getFlag();
		callbackIndex = inputSocketAtt.getCallbackIndex();
		inputBuffer = inputSocketAtt.getBuffer();
		if (inputBuffer != null) {
			inputCount = inputSocketAtt.getPostDataLength();
			inputPos = inputBuffer.length - inputCount;
		}

		inputStream = inputSocketAtt.inputStream;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class InputSocketAtt {

		/** id */
		protected Serializable id;

		/** buffer */
		protected byte[] buffer;

		/** flag */
		protected byte flag;

		/** callbackIndex */
		protected int callbackIndex;

		/** url */
		protected String url;

		/** postDataLength */
		protected int postDataLength;

		/** inputStream */
		protected InputStream inputStream;

		/**
		 * 空初始化
		 */
		protected InputSocketAtt() {
		}

		/**
		 * @param id
		 * @param buffer
		 * @param selSession
		 */
		public InputSocketAtt(Serializable id, byte[] buffer, SelSession selSession) {
			this(id, buffer, 0, selSession, null);
		}

		/**
		 * @param id
		 * @param buffer
		 * @param selSession
		 * @param inputStream
		 */
		public InputSocketAtt(Serializable id, byte[] buffer, SelSession selSession, InputStream inputStream) {
			this(id, buffer, 0, selSession, inputStream);
		}

		/**
		 * @param id
		 * @param buffer
		 * @param off
		 * @param selSession
		 * @param inputStream
		 */
		public InputSocketAtt(Serializable id, byte[] buffer, int off, SelSession selSession, InputStream inputStream) {
			this.id = id;
			this.buffer = buffer;
			this.flag = buffer[off];
			int headerlength = off + 1;
			if ((flag & SocketAdapter.STREAM_FLAG) != 0) {
				headerlength += 4;
			}

			if ((flag & SocketAdapter.CALLBACK_FLAG) != 0) {
				callbackIndex = KernelByte.getLength(buffer, headerlength);
				headerlength += 4;
			}

			if ((flag & SocketAdapter.POST_FLAG) != 0) {
				postDataLength = KernelByte.getLength(buffer, headerlength);
				headerlength += 4;
			}

			url = new String(buffer, headerlength, buffer.length - headerlength - postDataLength,
					ContextUtils.getCharset());
			this.inputStream = inputStream;
		}

		/**
		 * @return the id
		 */
		public Serializable getId() {
			return id;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @return the flag
		 */
		public byte getFlag() {
			return flag;
		}

		/**
		 * @return
		 */
		public InMethod getMethod() {
			return (flag & (SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG)) == 0 ? InMethod.GET : InMethod.POST;
		}

		/**
		 * @return the callbackIndex
		 */
		public int getCallbackIndex() {
			return callbackIndex;
		}

		/**
		 * @return the buffer
		 */
		public byte[] getBuffer() {
			return buffer;
		}

		/**
		 * @return the postDataLength
		 */
		public int getPostDataLength() {
			return postDataLength;
		}
	}

	/**
	 * @return the socketChannel
	 */
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	@Override
	public Object getAttribute(String name) {
		return getModel().get(name);
	}

	@Override
	public void setAttribute(String name, Object obj) {
		getModel().put(name, obj);
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public Map<String, Object> getParamMap() {
		return getModel();
	}

	@Override
	public String getParam(String name) {
		return KernelDyna.to(getModel().get(name), String.class);
	}

	@Override
	public InMethod getMethod() {
		return inputBuffer == null && inputStream == null ? InMethod.GET : InMethod.POST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#setStatus(int)
	 */
	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#paramDebug()
	 */
	@Override
	public boolean paramDebug() {
		return (flag & SocketAdapter.DEBUG_FLAG) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#getAddress()
	 */
	@Override
	public String getAddress() {
		return socketChannel.socket().getLocalAddress().getHostAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#getParams(java.lang.String)
	 */
	@Override
	public String[] getParams(String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return inputBuffer == null ? null : new ByteArrayInputStream(inputBuffer, inputPos, inputCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#getInput()
	 */
	@Override
	public String getInput() {
		if (input == null) {
			if (inputBuffer != null) {
				input = new String(inputBuffer, inputPos, inputCount, ContextUtils.getCharset());

			} else if (inputStream != null) {
				try {
					input = HelperIO.toString(inputStream, ContextUtils.getCharset());

				} catch (IOException e) {
					input = KernelLang.NULL_STRING;
				}

			} else {
				input = KernelLang.NULL_STRING;
			}
		}

		return input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#setContentTypeCharset(java.lang.String)
	 */
	@Override
	public void setContentTypeCharset(String contentTypeCharset) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String charset) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.Input#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/**
	 * 
	 */
	public void readyOutputStream() {
		if (outputStream == null && selSession != null) {
			boolean sended = false;
			final UtilActivePool activePool = selSession.getSocketBuffer().getActivePool();
			final ObjectTemplate<Integer> nextIndex = activePool.addObject();
			final PipedInputStream inputStream = new PipedInputStream();
			try {
				outputStream = new PipedOutputStream(inputStream);
				final int streamIndex = nextIndex.object;
				writeByteBuffer(getSocketBufferResolver(), selSession, socketChannel,
						(byte) (writeFlag(flag) | SocketAdapter.STREAM_FLAG), callbackIndex,
						KernelByte.getLengthBytes(streamIndex));
				ContextUtils.getThreadPoolExecutor().execute(new Runnable() {

					@Override
					public void run() {
						try {
							int postBuffLen = SocketAdapterSel.POST_BUFF_LEN;
							byte[] sendBufer = SocketBufferResolver.createByteBufferFull(getSocketBufferResolver(),
									socketChannel, 4 + postBuffLen, null, 0, 0);
							sendBufer[4] = SocketAdapter.STREAM_FLAG | SocketAdapter.POST_FLAG;
							KernelByte.setLength(sendBufer, 5, streamIndex);
							int len;
							try {
								while ((len = inputStream.read(sendBufer, 9, sendBufer.length)) > 0) {
									len += 5;
									KernelByte.setLength(sendBufer, 0, len);
									if (nextIndex.object == null
											|| !InputSocket.writeBuffer(socketChannel, sendBufer, 0, len)) {
										return;
									}
								}

							} catch (Exception e) {
								if (Environment.getEnvironment() == Environment.DEVELOP) {
									e.printStackTrace();
								}

								return;
							}

						} finally {
							activePool.remove(streamIndex);
							UtilPipedStream.closeCloseable(inputStream);
						}
					}
				});

				sended = true;

			} catch (Exception e) {
				if (Environment.getEnvironment() == Environment.DEVELOP) {
					e.printStackTrace();
				}

			} finally {
				if (!sended) {
					activePool.remove(nextIndex.object);
					UtilPipedStream.closeCloseable(inputStream);
				}
			}
		}
	}

	/**
	 * @return
	 */
	public abstract SocketBufferResolver getSocketBufferResolver();

	/**
	 * @param flag
	 */
	protected byte writeFlag(byte flag) {
		if (status != ServerStatus.ON_SUCCESS.getCode()) {
			flag |= SocketAdapter.ERROR_FLAG;
		}

		return flag;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		writeByteBuffer(getSocketBufferResolver(), selSession, socketChannel, writeFlag(flag), callbackIndex, b, off,
				len);
	}

	/**
	 * @param socketChannel
	 * @param buffer
	 * @return
	 */
	public static boolean writeBuffer(SocketChannel socketChannel, byte[] buffer) {
		return writeBuffer(socketChannel, buffer, 0, buffer.length);
	}

	/**
	 * @param socketChannel
	 * @param buffer
	 * @param offset
	 * @param length
	 * @return
	 */
	public static boolean writeBuffer(SocketChannel socketChannel, byte[] buffer, int offset, int length) {
		try {
			SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(buffer, offset, length));
			return true;

		} catch (IOException e) {
			SocketServer.close(socketChannel);
		}

		return false;
	}

	/**
	 * @param bufferResolver
	 * @param selSession
	 * @param socketChannel
	 * @param flag
	 * @param callbackIndex
	 * @param bytes
	 * @return
	 */
	public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
			SocketChannel socketChannel, int callbackIndex, byte[] bytes) {
		return writeByteBuffer(bufferResolver, selSession, socketChannel, (byte) 0, callbackIndex, bytes, 0,
				bytes.length);
	}

	/**
	 * @param bufferResolver
	 * @param selSession
	 * @param socketChannel
	 * @param flag
	 * @param callbackIndex
	 * @param bytes
	 * @return
	 */
	public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
			SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes) {
		return writeByteBuffer(bufferResolver, selSession, socketChannel, flag, callbackIndex, bytes, 0, bytes.length);
	}

	/**
	 * @param bufferResolver
	 * @param selSession
	 * @param socketChannel
	 * @param flag
	 * @param callbackIndex
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return
	 */
	public static boolean writeByteBuffer(SocketBufferResolver bufferResolver, SelSession selSession,
			SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes, int offset, int length) {
		int headerLength = flag == 0 ? 0 : callbackIndex == 0 ? 1 : 5;
		byte[] headerBytes = bufferResolver.createByteHeader(headerLength);
		if (headerBytes == null) {
			return false;
		}

		int headerOffset = headerBytes.length - headerLength;
		if (callbackIndex != 0) {
			flag |= SocketAdapter.CALLBACK_FLAG;
			KernelByte.setLength(headerBytes, headerOffset + 1, callbackIndex);
		}

		if (headerLength > 0) {
			headerBytes[headerOffset] = flag;
		}

		ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, headerBytes, bytes, offset,
				length);
		synchronized (socketChannel) {
			try {
				SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(headerBytes));
				SocketNIO.writeTimeout(socketChannel, byteBuffer);
				return true;

			} catch (Throwable e) {
				SocketServer.close(selSession, socketChannel);
			}
		}

		return false;
	}
}
