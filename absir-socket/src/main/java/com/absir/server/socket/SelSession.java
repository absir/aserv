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

public class SelSession {

    public static final Serializable UN_REGISTER_ID = new Character('0');

    protected SocketServer socketServer;

    protected SocketChannel socketChannel;

    protected long nextIdleTime;

    protected SocketBuffer socketBuffer;

    public SelSession(SocketServer server, SocketChannel channel) {
        socketServer = server;
        socketChannel = channel;
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public long getNextIdleTime() {
        return nextIdleTime;
    }

    public void setNextIdleTime(long nextIdleTime) {
        this.nextIdleTime = nextIdleTime;
    }

    public SocketBuffer getSocketBuffer() {
        return socketBuffer;
    }

    protected void setSocketBuffer(SocketBuffer socketBuffer) {
        this.socketBuffer = socketBuffer;
    }

    public void retainAcceptTimeout() {
        retainAcceptTimeout(socketServer.maxAcceptTime);
    }

    public void retainIdleTimeout() {
        retainIdleTimeout(socketServer.maxIdleTime);
    }

    public void retainAcceptTimeout(long timout) {
        nextIdleTime = UtilContext.getCurrentTime() + timout;
    }

    public void retainIdleTimeout(long timout) {
        nextIdleTime = UtilContext.getCurrentTime() + timout;
    }
}
