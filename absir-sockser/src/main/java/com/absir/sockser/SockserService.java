/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月10日 下午4:58:50
 */
package com.absir.sockser;

import java.io.Serializable;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.aserv.system.service.ActiveService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Stopping;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.server.socket.InputSocketContext;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
@Base
@Bean
public class SockserService extends ActiveService<JiServer, SocketSer> {

	/** ME */
	public static final SockserService ME = BeanFactoryUtils.get(SockserService.class);

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SockserService.class);

	/** serverClass */
	protected Class<? extends JiServer> serverClass = (Class<? extends JiServer>) SessionFactoryUtils
			.getEntityClass("JServer");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.ActiveService#getInstance()
	 */
	@Override
	protected ActiveService<JiServer, SocketSer> getInstance() {
		return ME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.service.ActiveService#findEntityClass()
	 */
	@Override
	protected Class<?> findEntityClass() {
		return serverClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.ActiveService#isClosed(java.lang.Object)
	 */
	@Override
	protected boolean isClosed(SocketSer activeContext) {
		return activeContext.isClosed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.ActiveService#createActiveContext(com.
	 * absir.aserv.system.bean.value.JiActive)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.ActiveService#updateActiveContext(com.
	 * absir.aserv.system.bean.value.JiActive, java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.system.service.ActiveService#closeActiveContext(java.io
	 * .Serializable, java.lang.Object)
	 */
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
