/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-17 下午4:28:46
 */
package com.absir.server.socket.resolver;

import com.absir.server.socket.SocketBuffer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author absir
 *
 */
public interface IBufferResolver {

    /**
     * @return
     */
    public SocketBuffer createSocketBuff();

    /**
     * @param socketBuffer
     * @param buffer
     * @param length
     */
    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length);

    /**
     * @param socketBuffer
     */
    public void readByteBufferDone(SocketBuffer socketBuffer);

    /**
     * @param headerLength
     * @return
     */
    public byte[] createByteHeader(int headerLength);

    /**
     * @param socketChannel
     * @param headerLength
     * @param headerBytes
     * @param bytes
     * @param offset
     * @param length
     * @return
     */
    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] headerBytes, byte[] bytes,
                                       int offset, int length);

}
