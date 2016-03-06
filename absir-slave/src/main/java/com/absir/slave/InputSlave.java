/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月9日 下午8:17:04
 */
package com.absir.slave;

import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelObject;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.socket.InputSocket;
import com.absir.server.socket.resolver.SocketBufferResolver;
import com.absir.slave.resolver.SlaveBufferResolver;

/**
 * @author absir
 *
 */
public class InputSlave extends InputSocket {

	/**
	 * @param input
	 * @return
	 */
	public static boolean onAuthentication(Input input) {
		if (input instanceof InputSlave) {
			return true;
		}

		String slvky = input.getParam("slvky");
		if (slvky != null) {
			InputSlaveAdapter adapter = InputSlaveContext.ME.getSlaveAdapter();
			Socket socket = adapter.getSocket();
			if (socket != null && KernelObject.equals(adapter.getSlaveKey(), slvky)) {
				if (input.getAddress().equals(socket.getInetAddress().getHostAddress())) {
					return true;

				} else {
					String slvhash = input.getParam("slvhash");
					if (slvhash != null
							&& slvhash.equals(HelperEncrypt.encryptionMD5(slvky, adapter.getKey().getBytes()))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @author absir
	 *
	 */
	public static class InputSlaveAtt extends InputSocketAtt {

		/** socketAdapter */
		protected SocketAdapter socketAdapter;

		/**
		 * @param id
		 * @param buffer
		 * @param inputStream
		 * @param socketAdapter
		 */
		public InputSlaveAtt(Serializable id, byte[] buffer, InputStream inputStream, SocketAdapter socketAdapter) {
			super(id, buffer, 5, null, inputStream);
			this.socketAdapter = socketAdapter;
		}
	}

	/** socketAdapter */
	protected SocketAdapter socketAdapter;

	/**
	 * @return the socketAdapter
	 */
	public SocketAdapter getSocketAdapter() {
		return socketAdapter;
	}

	/**
	 * @param socketAdapter
	 *            the socketAdapter to set
	 */
	public void setSocketAdapter(SocketAdapter socketAdapter) {
		this.socketAdapter = socketAdapter;
	}

	/**
	 * @param model
	 * @param inputSocketAtt
	 */
	public InputSlave(InModel model, InputSlaveAtt inputSocketAtt, SocketChannel socketChannel) {
		super(model, inputSocketAtt, inputSocketAtt.socketAdapter.getSocket().getChannel());
		this.socketAdapter = inputSocketAtt.socketAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.socket.InputSocket#writeFlag(byte)
	 */
	@Override
	protected byte writeFlag(byte flag) {
		return (byte) (super.writeFlag(flag) | SocketAdapter.RESPONSE_FLAG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.socket.InputSocketImpl#getSocketBufferResolver()
	 */
	@Override
	public SocketBufferResolver getSocketBufferResolver() {
		return SlaveBufferResolver.ME;
	}
}
