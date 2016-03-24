/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月2日 下午6:09:18
 */
package com.absir.server.socket.resolver;

import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

public interface IServerResolver {

    public long acceptTimeoutNIO(SocketChannel socketChannel) throws Throwable;

    public void doBeat(SocketChannel socketChannel, SelSession selSession, long currentTime);

    public void register(SocketChannel socketChannel, SelSession selSession, byte[] buffer, long currentTime) throws Throwable;

    public void receiveBeatNIO(SocketChannel socketChannel, SelSession selSession, long currentTime);

    public boolean receiveBufferNIO(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                    byte[] buffer, long currentTime);

    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession, SocketBuffer socketBuffer,
                                  byte[] buffer, long currentTime);

    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession, long currentTime);

}
