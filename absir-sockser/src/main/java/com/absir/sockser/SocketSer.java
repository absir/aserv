/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月10日 下午5:01:07
 */
package com.absir.sockser;

import com.absir.server.socket.SocketServer;

public class SocketSer extends SocketServer {

    protected JiServer server;

    public SocketSer(JiServer server) {
        this.server = server;
    }

    public JiServer getServer() {
        return server;
    }

    protected void setServer(JiServer server) {
        this.server = server;
    }
}
