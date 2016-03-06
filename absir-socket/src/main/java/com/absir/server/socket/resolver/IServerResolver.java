/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月2日 下午6:09:18
 */
package com.absir.server.socket.resolver;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;

/**
 * @author absir
 *
 */
public interface IServerResolver {

	/**
	 * MUST NIO
	 * 
	 * @param socketChannel
	 * @return
	 * @throws Throwable
	 */
	public long acceptTimeoutNIO(SocketChannel socketChannel) throws Throwable;

	/**
	 * @param socketChannel
	 * @param selSession
	 * @param contextTime
	 */
	public void doBeat(SocketChannel socketChannel, SelSession selSession, long contextTime);

	/**
	 * @param socketChannel
	 * @param selSession
	 * @param buffer
	 * @throws Throwable
	 */
	public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer) throws Throwable;

	/**
	 * @param socketChannel
	 * @param selSession
	 */
	public void reciveBeatNIO(SocketChannel socketChannel, SelSession selSession);

	/**
	 * MUST NIO
	 * 
	 * @param socketChannel
	 * @param selSession
	 * @param socketBuffer
	 * @param buffer
	 * @return
	 */
	public boolean receiveBufferNIO(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
			byte[] buffer);

	/**
	 * @param socketChannel
	 * @param selSession
	 * @param socketBuffer
	 * @param buffer
	 */
	public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
			byte[] buffer);

	/**
	 * @param id
	 * @param socketChannel
	 * @param selSession
	 */
	public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession);

}
