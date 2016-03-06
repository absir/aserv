/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年10月23日 下午8:30:36
 */
package com.absir.client;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author absir
 *
 */
public class SocketNIO {

	/** writeTimeout */
	private static long writeTimeout = 20000;

	/**
	 * @return the writeTimeout
	 */
	public static long getWriteTimeout() {
		return writeTimeout;
	}

	/**
	 * @param timeout
	 */
	public static void setWriteTimeout(long timeout) {
		if (timeout < 3000) {
			timeout = 3000;
		}

		writeTimeout = timeout;
	}

	/**
	 * @param socketChannel
	 * @param byteBuffer
	 * @return
	 * @throws IOException
	 */
	public static void writeTimeout(SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException {
		writeTimeout(socketChannel, byteBuffer, writeTimeout);
	}

	/**
	 * @param socketChannel
	 * @param byteBuffer
	 * @param writeTimeout
	 * @return
	 * @throws IOException
	 */
	public static void writeTimeout(SocketChannel socketChannel, ByteBuffer byteBuffer, long writeTimeout)
			throws IOException {
		int attempts = 0;
		SelectionKey key = null;
		Selector writeSelector = null;
		try {
			while (byteBuffer.hasRemaining()) {
				int len = socketChannel.write(byteBuffer);
				attempts++;
				if (len <= 0) {
					if (len < 0) {
						throw new EOFException();

					} else {
						if (writeSelector == null) {
							writeSelector = SelectorFactory.getSelector();
							if (writeSelector == null) {
								// continue using the main one
								continue;
							}

							key = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
						}

						if (writeSelector.select(writeTimeout) == 0) {
							if (attempts > 2)
								throw new IOException("writeTimeout");

						} else {
							attempts--;
						}
					}

				} else {
					attempts = 0;
				}
			}

		} finally {
			if (key != null) {
				key.cancel();
				key = null;
			}

			if (writeSelector != null) {
				writeSelector.selectNow();
				SelectorFactory.returnSelector(writeSelector);
			}
		}
	}

}
