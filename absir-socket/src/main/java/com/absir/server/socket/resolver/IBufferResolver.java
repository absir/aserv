/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-2-17 下午4:28:46
 */
package com.absir.server.socket.resolver;

import com.absir.core.kernel.KernelLang;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IBufferResolver {

    public SocketBuffer createSocketBuff();

    public int readByteBuffer(SocketBuffer socketBuffer, byte[] buffer, int position, int length);

    public void readByteBufferDone(SocketBuffer socketBuffer);

    public ByteBuffer createByteBuffer(SocketChannel socketChannel, int headerLength, byte[] bytes,
                                       int offset, int length);

    public byte[] createByteHeader(int headerLength, ByteBuffer byteBuffer);

    public int getPostBufferLen();

    public void writeInputStream(final SelSession selSession, final KernelLang.ObjectTemplate<Integer> template, final int streamIndex, final InputStream inputStream);
}
