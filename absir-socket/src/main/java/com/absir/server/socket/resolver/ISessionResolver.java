/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月8日 下午4:29:50
 */
package com.absir.server.socket.resolver;

import com.absir.server.socket.SelSession;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * @author absir
 *
 */
public interface ISessionResolver {

    /**
     * @param socketChannel
     * @return
     * @throws Throwable
     */
    public long acceptTimeout(SocketChannel socketChannel) throws Throwable;

    /**
     * @param socketChannel
     * @param selSession
     * @param contextTime
     */
    public void idle(SocketChannel socketChannel, SelSession selSession, long contextTime);

    /**
     * @param socketChannel
     * @param selSession
     * @throws Throwable
     */
    public void register(SocketChannel socketChannel, SelSession selSession) throws Throwable;

    /**
     * @param socketChannel
     * @param selSession
     * @throws Throwable
     */
    public void receiveByteBuffer(SocketChannel socketChannel, SelSession selSession) throws Throwable;

    /**
     * @param id
     * @param socketChannel
     * @param selSession
     * @throws Throwable
     */
    public void unRegister(Serializable id, SocketChannel socketChannel, SelSession selSession) throws Throwable;

}
