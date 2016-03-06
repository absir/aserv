/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年10月8日 下午4:29:50
 */
package com.absir.server.socket.resolver;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilContext.RunableGuaranted;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.SocketServer;

/**
 * @author absir
 *
 */
public class SocketSessionResolver implements ISessionResolver {

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SocketSessionResolver.class);

	/** bufferResolver */
	protected static IBufferResolver bufferResolver;

	/** serverResolver */
	protected static IServerResolver serverResolver;

	/** beat */
	protected static byte[] beat;

	/** beatBuffer */
	protected static byte[] beatBuffer;

	/** okBuffer */
	protected static byte[] okBuffer;

	/** failedBuffer */
	protected static byte[] failedBuffer;

	/**
	 * @return the beat
	 */
	public static byte[] getBeat() {
		return beat;
	}

	/**
	 * @return the beatBuffer
	 */
	public static byte[] getBeatBuffer() {
		return beatBuffer;
	}

	/**
	 * @return the okBuffer
	 */
	public static byte[] getOkBuffer() {
		return okBuffer;
	}

	/**
	 * @return the failedBuffer
	 */
	public static byte[] getFailedBuffer() {
		return failedBuffer;
	}

	/**
	 * @param bufferResolver
	 * @param sessionResolver
	 * @param beat
	 * @param ok
	 * @param failed
	 */
	public static void setResolver(IBufferResolver bufferResolver, IServerResolver serverResolver, byte[] beat,
			byte[] ok, byte[] failed) {
		SocketSessionResolver.bufferResolver = bufferResolver;
		SocketSessionResolver.serverResolver = serverResolver;
		SocketSessionResolver.beat = beat;
		beatBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, beat, 0, beat.length);
		okBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, ok, 0, ok.length);
		failedBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, failed, 0, failed.length);
	}

	/**
	 * @return the bufferResolver
	 */
	public IBufferResolver getBufferResolver() {
		return bufferResolver;
	}

	/**
	 * @return the serverResolver
	 */
	public IServerResolver getServerResolver() {
		return serverResolver;
	}

	/**
	 * @param selSession
	 * @param socketChannel
	 * @param id
	 * @return
	 */
	protected boolean writeSuccess(SelSession selSession, SocketChannel socketChannel, Serializable id) {
		return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, okBuffer);
	}

	/**
	 * @param selSession
	 * @param socketChannel
	 * @return
	 */
	protected boolean writeFailed(SelSession selSession, SocketChannel socketChannel) {
		return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, failedBuffer);
	}

	/**
	 * @param selSession
	 * @param socketChannel
	 * @param id
	 * @return
	 */
	protected boolean writeBeat(SelSession selSession, SocketChannel socketChannel, Serializable id) {
		return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, beatBuffer);
	}

	/** propertyFilter */
	protected PropertyFilter propertyFilter;

	/**
	 * @param includes
	 * @param excludes
	 */
	public void setSessionFilters(String[] includes, String[] excludes) {
		PropertyFilter filter = null;
		if (includes != null && includes.length > 0) {
			filter = new PropertyFilter();
			for (String include : includes) {
				filter.inlcude(include);
			}
		}

		if (excludes != null && excludes.length > 0) {
			if (filter == null) {
				filter = new PropertyFilter();
			}

			for (String exclude : excludes) {
				filter.inlcude(exclude);
			}
		}

		propertyFilter = filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#acceptTimeout(java
	 * .nio.channels.SocketChannel)
	 */
	@Override
	public long acceptTimeout(SocketChannel socketChannel) throws Throwable {
		if (propertyFilter == null || propertyFilter.isMatchPath(socketChannel.socket().getInetAddress().toString())) {
			return getServerResolver().acceptTimeoutNIO(socketChannel);
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#idle(java.nio.
	 * channels.SocketChannel, com.absir.server.socket.SelSession, long)
	 */
	@Override
	public void idle(final SocketChannel socketChannel, final SelSession selSession, final long contextTime) {
		selSession.retainIdleTimeout();
		UtilContext.getThreadPoolExecutor().execute(new Runnable() {

			@Override
			public void run() {
				if (SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, beatBuffer)) {
					getServerResolver().doBeat(socketChannel, selSession, contextTime);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#register(java.nio.
	 * channels.SocketChannel, com.absir.server.socket.SelSession)
	 */
	@Override
	public void register(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
		final SocketBuffer socketBuffer = selSession.getSocketBuffer();
		socketBuffer.setId(SelSession.UN_REGISTER_ID);
		final byte[] buffer = socketBuffer.getBuff();
		socketBuffer.setBuff(null);
		UtilContext.getThreadPoolExecutor().execute(new Runnable() {

			@Override
			public void run() {
				Serializable id = null;
				try {
					getServerResolver().register(socketChannel, selSession, buffer);
					id = socketBuffer.getId();
					if (id == SelSession.UN_REGISTER_ID) {
						id = null;
						socketBuffer.setId(null);
					}

					if (id == null) {
						if (writeFailed(selSession, socketChannel)) {
							selSession.retainIdleTimeout();
						}

					} else {
						if (writeSuccess(selSession, socketChannel, id)) {
							selSession.retainIdleTimeout();
						}
					}

				} catch (Throwable e) {
					if (SocketServer.isCloseDebug()) {
						e.printStackTrace();
					}

				} finally {
					if (!(id == null || socketChannel.isConnected())) {
						SocketServer.close(selSession, socketChannel);
					}
				}
			}
		});
	}

	/**
	 * @param socketChannel
	 * @param id
	 * @param buffer
	 * @param socketBuffer
	 */
	protected boolean doDenied(SocketChannel socketChannel, Serializable id, byte[] buffer, SocketBuffer socketBuffer) {
		return id == null || id == SelSession.UN_REGISTER_ID;
	}

	/**
	 * @param socketChannel
	 * @param socketBuffer
	 * @param buffer
	 * @return
	 */
	protected boolean isBeat(SocketChannel socketChannel, SocketBuffer socketBuffer, byte[] buffer) {
		int length = beat.length;
		if (buffer.length == length) {
			for (int i = 0; i < length; i++) {
				if (buffer[i] != beat[i]) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#receiveByteBuffer(
	 * java.nio.channels.SocketChannel, com.absir.server.socket.SelSession)
	 */
	@Override
	public void receiveByteBuffer(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
		final SocketBuffer socketBuffer = selSession.getSocketBuffer();
		final Serializable id = socketBuffer.getId();
		final byte[] buffer = socketBuffer.getBuff();
		socketBuffer.setBuff(null);
		if (doDenied(socketChannel, id, buffer, socketBuffer)) {
			return;
		}

		if (isBeat(socketChannel, socketBuffer, buffer)) {
			getServerResolver().reciveBeatNIO(socketChannel, selSession);
			return;
		}

		if (getServerResolver().receiveBufferNIO(socketChannel, selSession, socketBuffer, buffer)) {
			return;
		}

		if (socketBuffer.addBufferQueue(buffer)) {
			return;
		}

		UtilContext.getThreadPoolExecutor().execute(new Runnable() {

			@Override
			public void run() {
				byte[] queueBuffer = buffer;
				while (queueBuffer != null) {
					try {
						getServerResolver().receiveByteBuffer(socketChannel, selSession, socketBuffer, queueBuffer);

					} catch (Throwable e) {
						LOGGER.error("receiveByteBuffer", e);
					}

					if (!socketChannel.isConnected()) {
						break;
					}

					queueBuffer = socketBuffer.readBufferQueue();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.socket.resolver.SocketSessionResolver#unRegister(java.io
	 * .Serializable, java.nio.channels.SocketChannel,
	 * com.absir.server.socket.SelSession)
	 */
	@Override
	public void unRegister(final Serializable id, final SocketChannel socketChannel, final SelSession selSession)
			throws Throwable {
		UtilContext.getThreadPoolExecutor().execute(new RunableGuaranted() {

			@Override
			public void run() {
				getServerResolver().unRegister(id, socketChannel, selSession);
			}
		});
	}
}
