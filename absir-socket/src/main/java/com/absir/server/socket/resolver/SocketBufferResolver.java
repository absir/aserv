/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月2日 上午11:19:48
 */
package com.absir.server.socket.resolver;

import com.absir.client.SocketNIO;
import com.absir.core.kernel.KernelByte;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.SocketServer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketBufferResolver implements IBufferResolver {

    protected static long bufferMax = 10240;

    public static long getBufferMax() {
        return bufferMax;
    }

    public static void setBufferMax(long max) {
        if (max < 8) {
            max = 8;
        }

        bufferMax = max;
    }

    public static byte[] createByteBufferFull(IBufferResolver bufferResolver, SocketChannel socketChannel,
                                              int headerLength, byte[] bytes, int offset, int length) {
        byte[] headers = bufferResolver.createByteHeader(headerLength);
        ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, headers, bytes, offset,
                length);

        int headerLen = headers == null ? 0 : headers.length;
        int bufferOffset = byteBuffer.arrayOffset();
        int bufferLen = headers == null ? 0 : byteBuffer.limit() - bufferOffset;
        byte[] bufferFull = new byte[headerLen + bufferLen];
        if (headerLen > 0) {
            System.arraycopy(headers, 0, bufferFull, 0, headerLen);
        }

        if (bufferLen > 0) {
            System.arraycopy(byteBuffer.array(), bufferOffset, bufferFull, headerLen, bufferLen);
        }

        return bufferFull;
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes) {
        return writeBufferTimeout(selSession, socketChannel, bytes, 0, bytes.length);
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes,
                                             int offset, int length) {
        return writeBufferTimeout(selSession, socketChannel, bytes, offset, length, SocketNIO.getWriteTimeout());
    }

    public static boolean writeBufferTimeout(SelSession selSession, SocketChannel socketChannel, byte[] bytes,
                                             int offset, int length, long writeTimeout) {
        synchronized (socketChannel) {
            try {
                SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(bytes, offset, length), writeTimeout);
                return true;

            } catch (Throwable e) {
                SocketServer.close(selSession, socketChannel);
            }
        }

        return false;
    }

    public static boolean writeBufferTimeout(SelSession selSession, IBufferResolver bufferResolver,
                                             SocketChannel socketChannel, int headerLength, byte[] bytes, int offset, int length) {
        return writeBufferTimeout(selSession, bufferResolver, socketChannel, headerLength, bytes, offset, length,
                SocketNIO.getWriteTimeout());
    }

    public static boolean writeBufferTimeout(SelSession selSession, IBufferResolver bufferResolver,
                                             SocketChannel socketChannel, int headerLength, byte[] bytes, int offset, int length, long writeTimeout) {
        byte[] headers = bufferResolver.createByteHeader(headerLength);
        ByteBuffer byteBuffer = bufferResolver.createByteBuffer(socketChannel, headerLength, headers, bytes, offset,
                length);
        synchronized (socketChannel) {
            try {
                if (headers != null) {
                    SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(headers), writeTimeout);
                }

                SocketNIO.writeTimeout(socketChannel, byteBuffer, writeTimeout);
                return true;

            } catch (Throwable e) {
                SocketServer.close(selSession, socketChannel);
            }
        }

        return false;
    }

    @Override
    public SocketBuffer createSocketBuff() {
        return new SocketBuffer();
    }

    @Override
    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length) {
        for (; position < length; position++) {
            if (socketBuffer.getBuff() == null) {
                int lengthIndex = socketBuffer.getLengthIndex();
                if (lengthIndex < 4) {
                    int buffLength = buffer[position] & 0xFF;
                    if (lengthIndex > 0) {
                        buffLength = socketBuffer.getLength() + (buffLength << (8 * lengthIndex));
                    }

                    socketBuffer.setLength(buffLength);
                    socketBuffer.setLengthIndex(++lengthIndex);
                    if (lengthIndex == 4) {
                        if (buffLength > 0 && buffLength < bufferMax) {
                            socketBuffer.setBuffLengthIndex(0);
                            socketBuffer.setBuff(new byte[buffLength]);

                        } else {
                            socketBuffer.setLength(0);
                            socketBuffer.setLengthIndex(0);
                        }
                    }
                }

            } else {
                int buffLengthIndex = socketBuffer.getBuffLengthIndex();
                socketBuffer.getBuff()[buffLengthIndex] = buffer[position];
                socketBuffer.setBuffLengthIndex(++buffLengthIndex);
                if (buffLengthIndex >= socketBuffer.getLength()) {
                    break;
                }
            }
        }

        return position;
    }

    @Override
    public void readByteBufferDone(SocketBuffer socketBuffer) {
        socketBuffer.setBuff(null);
        socketBuffer.setLength(0);
        socketBuffer.setLengthIndex(0);
    }

    @Override
    public byte[] createByteHeader(int headerLength) {
        return new byte[4 + headerLength];
    }

    @Override
    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] headerBytes, byte[] bytes,
                                       int offset, int length) {
        KernelByte.setLength(headerBytes, 0, headerLength + length - offset);
        return ByteBuffer.wrap(bytes, offset, length);
    }
}
