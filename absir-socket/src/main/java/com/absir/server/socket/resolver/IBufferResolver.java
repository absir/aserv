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

public interface IBufferResolver {

    public SocketBuffer createSocketBuff();

    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length);

    public void readByteBufferDone(SocketBuffer socketBuffer);

    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] bytes,
                                       int offset, int length);

    public byte[] createByteHeader(int headerLength, ByteBuffer byteBuffer);
}
