/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月10日 下午4:58:50
 */
package com.absir.sockser;

import com.absir.aserv.system.service.ActiveService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Stopping;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.server.socket.InputSocketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetAddress;

@SuppressWarnings("unchecked")
@Base
@Bean
public class SockserService extends ActiveService<JiServer, SocketSer> {

    public static final SockserService ME = BeanFactoryUtils.get(SockserService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SockserService.class);

    protected Class<? extends JiServer> serverClass = (Class<? extends JiServer>) SessionFactoryUtils
            .getEntityClass("JServer");

    @Override
    protected ActiveService<JiServer, SocketSer> getInstance() {
        return ME;
    }

    @Override
    protected Class<?> findEntityClass() {
        return serverClass;
    }

    @Override
    protected boolean isClosed(SocketSer activeContext) {
        return activeContext.isClosed();
    }

    @Override
    protected SocketSer createActiveContext(JiServer active) {
        SocketSer ser = new SocketSer(active);
        InputSocketContext context = InputSocketContext.ME;
        try {
            ser.start(InputSocketContext.getAcceptTimeout(), InputSocketContext.getIdleTimeout(), active.getPort(),
                    context.getBacklog(), InetAddress.getByName(active.getIp()), context.getBufferSize(),
                    context.getReceiveBufferSize(), context.getBufferSize(), context.getBufferResolver(),
                    context.getSessionResolver());

        } catch (Exception e) {
            LOGGER.error("start server failed " + active.getIp() + " => " + active.getPort(), e);
        }

        return ser;
    }

    @Override
    protected SocketSer updateActiveContext(JiServer active, SocketSer activeContext) {
        JiServer old = activeContext.getServer();
        if (old.getPort() != active.getPort() || !KernelObject.equals(old.getIp(), active.getIp())) {
            return null;

        } else {
            activeContext.setServer(active);
        }

        return activeContext;
    }

    @Override
    protected void closeActiveContext(Serializable id, SocketSer activeContext) {
        activeContext.close();
    }

    /**
     * 关闭服务
     */
    @Stopping
    public void closeAllContext() {
        for (SocketSer socketSer : activerMap.getOnlineActiveContexts().values()) {
            socketSer.close();
        }
    }

}
