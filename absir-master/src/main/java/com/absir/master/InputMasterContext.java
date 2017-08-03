/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月8日 下午4:57:44
 */
package com.absir.master;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.core.kernel.KernelDyna;
import com.absir.master.resolver.MasterBufferResolver;
import com.absir.master.resolver.MasterSessionResolver;
import com.absir.server.socket.SocketServer;
import com.absir.server.socket.SocketServerContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

@Base
@Bean
public class InputMasterContext {

    public static final InputMasterContext ME = BeanFactoryUtils.get(InputMasterContext.class);

    @Value("master.accept.timeout")
    protected static long acceptTimeout = 120000;

    @Value("master.idle.timeout")
    protected static long idleTimeout = 30000;

    @Value("server.master.ip")
    protected String ip;

    @Value("server.master.port")
    protected int port = 28890;

    @Value("server.master.backlog")
    protected int backlog = 50;

    @Value("server.master.bufferSize")
    protected int bufferSize = 1024;

    @Value("server.master.receiveBufferSize")
    protected int receiveBufferSize = 2048;

    @Value("server.master.sendBufferSize")
    protected int sendBufferSize = 2048;

    @Value("server.master.host")
    protected String[] hosts = null;

    @Value("server.master.host.exclude")
    protected String[] excludes = null;

    @Value("server.master.key")
    protected String key = "absir@qq.com";
    protected SocketServer socketServer;

    @Inject
    protected MasterSessionResolver sessionResolver;

    private SocketServerContext<MasterChannelContext> serverContext = new SocketServerContext<MasterChannelContext>();

    public SocketServerContext<MasterChannelContext> getServerContext() {
        return serverContext;
    }

    protected SocketServer createSocketServer() {
        return new SocketServer();
    }

    public MasterSessionResolver getSessionResolver() {
        return sessionResolver;
    }

    /**
     * 开始服务
     */
    @Started
    protected void started() throws IOException {
        socketServer = createSocketServer();
        sessionResolver.setSessionFilters(hosts, excludes);
        socketServer.start(acceptTimeout, idleTimeout, port, backlog, InetAddress.getByName(ip), bufferSize,
                receiveBufferSize, sendBufferSize, MasterBufferResolver.ME, getSessionResolver());
    }

    /**
     * 关闭服务
     */
    @Stopping
    protected void stopping() {
        socketServer.close();
    }

    public String getKey() {
        return key;
    }

    public void registerSlaveKey(String id, byte[] secrets, String validate, String[] params,
                                 SocketChannel socketChannel, long currentTime) {
        MasterChannelContext channelContext = new MasterChannelContext(id, socketChannel);
        channelContext.slaveKey = validate;
        serverContext.loginSocketChannel(id, channelContext);
        if (params.length > 2) {
            long serverTime = KernelDyna.to(params[2], long.class);
            channelContext.getMasterChannelAdapter().setRegistered(true, serverTime);
        }
    }

    public MasterChannelContext unRegisterSlaveKey(String id, SocketChannel socketChannel, long currentTime) {
        return serverContext.logoutSocketChannel(id, socketChannel);
    }

    public String getSlaveKey(Serializable id) {
        MasterChannelContext channelContext = serverContext.getChannelContexts().get(id);
        return channelContext == null ? null : channelContext.slaveKey;
    }

    public SocketChannel getSlaveSocketChannel(Serializable id) {
        MasterChannelContext channelContext = serverContext.getChannelContexts().get(id);
        return channelContext == null ? null : channelContext.getChannel();
    }

    protected MasterRpcAdapter createMasterRpcAdapter(Serializable id) {
        return new MasterRpcAdapter(new MasterChannelAdapter());
    }

    public MasterRpcAdapter getMasterRpcAdapter(Serializable id) {
        MasterChannelContext channelContext = serverContext.getChannelContexts().get(id);
        return channelContext == null ? null : channelContext.getMasterRpcAdapter();
    }

}
