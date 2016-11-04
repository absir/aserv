/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-17 下午4:28:46
 */
package com.absir.server.socket.resolver;

import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IBufferResolver {

    public SocketBuffer createSocketBuff();

    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length);

    public void readByteBufferDone(SocketBuffer socketBuffer);

    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] bytes,
                                       int offset, int length);

    public byte[] createByteHeader(int headerLength, ByteBuffer byteBuffer);

    public boolean receiveStreamNIO(final SocketChannel socketChannel, final SelSession selSession, final SocketBuffer socketBuffer, final byte flag, final byte[] buffer, final long currentTime, final int streamMax, final IServerDispatch serverDispatch);

    public boolean writeByteBuffer(final SelSession selSession, final SocketChannel socketChannel, byte flag, int callbackIndex, byte[] bytes, int offset, int length, final InputStream inputStream, Closeable pipeOutput);

    public interface IServerDispatch {

        public void doDispatch(SelSession selSession, SocketChannel socketChannel, Serializable id, byte[] buffer, byte flag, int off,
                               SocketBuffer socketBuffer, InputStream inputStream, long currentTime);

    }

}
