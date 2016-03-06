/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月4日 下午4:57:36
 */
package com.absir.server.socket;

import java.nio.channels.SocketChannel;

import com.absir.client.SocketAdapter;
import com.absir.server.in.InModel;
import com.absir.server.socket.resolver.InputBufferResolver;
import com.absir.server.socket.resolver.SocketBufferResolver;

/**
 * @author absir
 *
 */
public class InputSocketImpl extends InputSocket {

	/**
	 * @param model
	 * @param inputSocketAtt
	 * @param socketChannel
	 */
	public InputSocketImpl(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
		super(model, inputSocketAtt, socketChannel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.socket.InputSocket#getSocketBufferResolver()
	 */
	@Override
	public SocketBufferResolver getSocketBufferResolver() {
		return InputBufferResolver.ME;
	}

	/**
	 * @param socketChannel
	 * @param success
	 * @param callbackIndex
	 * @param bytes
	 * @return
	 */
	public static boolean writeByteBufferSuccess(SelSession selSession, SocketChannel socketChannel, boolean success,
			int callbackIndex, byte[] bytes) {
		return writeByteBuffer(InputBufferResolver.ME, selSession, socketChannel,
				success == true ? 0 : SocketAdapter.ERROR_FLAG, callbackIndex, bytes);
	}
}
