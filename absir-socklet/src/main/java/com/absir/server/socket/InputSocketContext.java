/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月4日 下午4:09:09
 */
package com.absir.server.socket;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.server.socket.resolver.InputBufferResolver;
import com.absir.server.socket.resolver.InputSessionResolver;
import com.absir.server.socket.resolver.SocketServerResolver;
import com.absir.server.socket.resolver.SocketSessionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

@Base
@Bean
public class InputSocketContext {

    public static final InputSocketContext ME = BeanFactoryUtils.get(InputSocketContext.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(InputSocketContext.class);

    @Value("socket.accept.debug")
    protected static boolean acceptDebug;

    @Value("socket.close.debug")
    protected static boolean closeDebug;

    @Value("socket.session.delay")
    protected static long sessionDelay = 5000;

    @Value("socket.accept.timeout")
    protected static long acceptTimeout = 120000;

    @Value("socket.idle.timeout")
    protected static long idleTimeout = 30000;

    @Value("server.socket.open")
    protected boolean open;

    @Value("server.socket.port")
    protected int port = 18890;

    @Value("server.socket.ip")
    protected String ip = "";

    @Value("server.socket.backlog")
    protected int backlog = 50;

    @Value("server.socket.bufferSize")
    protected int bufferSize = 1024;

    @Value("server.socket.receiveBufferSize")
    protected int receiveBufferSize = 2048;

    @Value("server.socket.sendBufferSize")
    protected int sendBufferSize = 2048;

    @Value("server.socket.beat")
    protected byte[] beat = "b".getBytes();

    @Value("server.socket.ok")
    protected byte[] ok = "ok".getBytes();

    @Value("server.socket.fail")
    protected byte[] failed = "failed".getBytes();

    @Inject
    protected InputBufferResolver bufferResolver;

    @Inject
    protected InputSessionResolver sessionResolver;

    @Inject
    protected SocketServerResolver serverResolver;

    private SocketServer socketServer;

    public static boolean isAcceptDebug() {
        return acceptDebug;
    }

    public static boolean isCloseDebug() {
        return closeDebug;
    }

    public static long getSessionDelay() {
        return sessionDelay;
    }

    public static long getAcceptTimeout() {
        return acceptTimeout;
    }

    public static long getIdleTimeout() {
        return idleTimeout;
    }

    public boolean isOpen() {
        return open;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public int getBacklog() {
        return backlog;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public byte[] getBeat() {
        return beat;
    }

    public byte[] getOk() {
        return ok;
    }

    public byte[] getFailed() {
        return failed;
    }

    public InputBufferResolver getBufferResolver() {
        return bufferResolver;
    }

    public InputSessionResolver getSessionResolver() {
        return sessionResolver;
    }

    public SocketServerResolver getServerResolver() {
        return serverResolver;
    }

    @Inject
    protected void init() {
        SocketServer.setAcceptDebug(acceptDebug);
        SocketServer.setCloseDebug(closeDebug);
        SocketServer.setSessionDelay(sessionDelay);
        SocketSessionResolver.setResolver(bufferResolver, serverResolver, beat, ok, failed);
    }

    @Started
    protected void started() {
        if (open) {
            socketServer = new SocketServer();
            try {
                socketServer.start(acceptTimeout, idleTimeout, port, backlog, InetAddress.getByName(ip), bufferSize,
                        receiveBufferSize, sendBufferSize, bufferResolver, sessionResolver);

            } catch (Exception e) {
                LOGGER.error("start optional server", e);
            }
        }
    }

    @Stopping
    protected void stopping() {
        if (socketServer != null) {
            socketServer.close();
        }
    }
}
