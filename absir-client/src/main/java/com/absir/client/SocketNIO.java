/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月23日 下午8:30:36
 */
package com.absir.client;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketNIO {

    public static boolean _debugInfo;

    private static long writeTimeout = 20000;

    public static long getWriteTimeout() {
        return writeTimeout;
    }

    public static void setWriteTimeout(long timeout) {
        if (timeout < 3000) {
            timeout = 3000;
        }

        writeTimeout = timeout;
    }

    public static final void writeTimeout(SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException {
        writeTimeout(socketChannel, byteBuffer, writeTimeout);
    }

    public static final void writeTimeout(SocketChannel socketChannel, ByteBuffer byteBuffer, long writeTimeout)
            throws IOException {
        if (byteBuffer.limit() - byteBuffer.position() <= 0) {
            return;
        }

        int attempts = 0;
        SelectionKey key = null;
        Selector writeSelector = null;
        try {
            if (_debugInfo) {
                _debugInfo = false;

            } else {
                //new Exception().printStackTrace();
                //SocketAdapter._debugInfo("SocketNIO send  => " + Arrays.toString(Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit())));
            }

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
