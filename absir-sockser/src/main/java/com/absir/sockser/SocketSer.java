/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月10日 下午5:01:07
 */
package com.absir.sockser;

import com.absir.server.socket.SocketServer;

/**
 * @author absir
 */
public class SocketSer extends SocketServer {

    /**
     * server
     */
    protected JiServer server;

    /**
     * @param server
     */
    public SocketSer(JiServer server) {
        this.server = server;
    }

    /**
     * @return the server
     */
    public JiServer getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    protected void setServer(JiServer server) {
        this.server = server;
    }
}
