/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年8月21日 下午3:14:14
 */
package com.absir.server.socket;

import com.absir.core.util.UtilContext;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * @author absir
 */
public class SelSession {

    /**
     * UN_REGISTER_ID
     */
    public static final Serializable UN_REGISTER_ID = new Character('0');

    /**
     * socketServer
     */
    protected SocketServer socketServer;

    /**
     * socketChannel
     */
    protected SocketChannel socketChannel;

    /**
     * nextIdleTime
     */
    protected long nextIdleTime;

    /**
     * socketBuffer
     */
    protected SocketBuffer socketBuffer;

    /**
     * @param server
     * @param channel
     */
    public SelSession(SocketServer server, SocketChannel channel) {
        socketServer = server;
        socketChannel = channel;
    }

    /**
     * @return the socketServer
     */
    public SocketServer getSocketServer() {
        return socketServer;
    }

    /**
     * @return the socketChannel
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * @return the nextIdleTime
     */
    public long getNextIdleTime() {
        return nextIdleTime;
    }

    /**
     * @param nextIdleTime the nextIdleTime to set
     */
    public void setNextIdleTime(long nextIdleTime) {
        this.nextIdleTime = nextIdleTime;
    }

    /**
     * @return the socketBuffer
     */
    public SocketBuffer getSocketBuffer() {
        return socketBuffer;
    }

    /**
     * @param socketBuffer the socketBuffer to set
     */
    protected void setSocketBuffer(SocketBuffer socketBuffer) {
        this.socketBuffer = socketBuffer;
    }

    /**
     *
     */
    public void retainAcceptTimeout() {
        retainAcceptTimeout(socketServer.maxAcceptTime);
    }

    /**
     *
     */
    public void retainIdleTimeout() {
        retainIdleTimeout(socketServer.maxIdleTime);
    }

    /**
     * @param timout
     */
    public void retainAcceptTimeout(long timout) {
        nextIdleTime = UtilContext.getCurrentTime() + timout;
    }

    /**
     * @param timout
     */
    public void retainIdleTimeout(long timout) {
        nextIdleTime = UtilContext.getCurrentTime() + timout;
    }
}
