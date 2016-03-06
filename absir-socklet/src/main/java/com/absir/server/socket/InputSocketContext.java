/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月4日 下午4:09:09
 */
package com.absir.server.socket;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.server.socket.resolver.InputBufferResolver;
import com.absir.server.socket.resolver.InputSessionResolver;
import com.absir.server.socket.resolver.SocketServerResolver;
import com.absir.server.socket.resolver.SocketSessionResolver;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputSocketContext {

	/** ME */
	public static final InputSocketContext ME = BeanFactoryUtils.get(InputSocketContext.class);

	/** LOGGER */
	protected static final Logger LOGGER = LoggerFactory.getLogger(InputSocketContext.class);

	@Value("socket.accept.debug")
	protected static boolean acceptDebug;

	@Value("socket.close.debug")
	protected static boolean closeDebug;

	@Value("socket.session.delay")
	protected static long sessionDelay = 5000;

	/** acceptTimeout */
	@Value("socket.accept.timeout")
	protected static long acceptTimeout = 120000;

	/** idleTimeout */
	@Value("socket.idle.timeout")
	protected static long idleTimeout = 30000;

	/**
	 * @return the acceptDebug
	 */
	public static boolean isAcceptDebug() {
		return acceptDebug;
	}

	/**
	 * @return the closeDebug
	 */
	public static boolean isCloseDebug() {
		return closeDebug;
	}

	/**
	 * @return the sessionDelay
	 */
	public static long getSessionDelay() {
		return sessionDelay;
	}

	/**
	 * @return the acceptTimeout
	 */
	public static long getAcceptTimeout() {
		return acceptTimeout;
	}

	/**
	 * @return the idleTimeout
	 */
	public static long getIdleTimeout() {
		return idleTimeout;
	}

	/** open */
	@Value("server.socket.open")
	protected boolean open;

	/** port */
	@Value("server.socket.port")
	protected int port = 18890;

	/** ip */
	@Value("server.socket.ip")
	protected String ip = "";

	/** backlog */
	@Value("server.socket.backlog")
	protected int backlog = 50;

	/** bufferSize */
	@Value("server.socket.bufferSize")
	protected int bufferSize = 1024;

	/** bufferSize */
	@Value("server.socket.receiveBufferSize")
	protected int receiveBufferSize = 2048;

	/** bufferSize */
	@Value("server.socket.sendBufferSize")
	protected int sendBufferSize = 2048;

	/** beat */
	@Value("server.socket.beat")
	protected byte[] beat = "b".getBytes();

	/** ok */
	@Value("server.socket.ok")
	protected byte[] ok = "ok".getBytes();

	/** failed */
	@Value("server.socket.fail")
	protected byte[] failed = "failed".getBytes();

	/** bufferResolver */
	@Inject
	protected InputBufferResolver bufferResolver;

	/** sessionResolver */
	@Inject
	protected InputSessionResolver sessionResolver;

	/** serverResolver */
	@Inject
	protected SocketServerResolver serverResolver;

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the backlog
	 */
	public int getBacklog() {
		return backlog;
	}

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @return the receiveBufferSize
	 */
	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	/**
	 * @return the sendBufferSize
	 */
	public int getSendBufferSize() {
		return sendBufferSize;
	}

	/**
	 * @return the beat
	 */
	public byte[] getBeat() {
		return beat;
	}

	/**
	 * @return the ok
	 */
	public byte[] getOk() {
		return ok;
	}

	/**
	 * @return the failed
	 */
	public byte[] getFailed() {
		return failed;
	}

	/**
	 * @return the bufferResolver
	 */
	public InputBufferResolver getBufferResolver() {
		return bufferResolver;
	}

	/**
	 * @return the sessionResolver
	 */
	public InputSessionResolver getSessionResolver() {
		return sessionResolver;
	}

	/**
	 * @return the serverResolver
	 */
	public SocketServerResolver getServerResolver() {
		return serverResolver;
	}

	/** socketServer */
	private SocketServer socketServer;

	/**
	 * 
	 */
	@Inject
	protected void init() {
		SocketServer.setAcceptDebug(acceptDebug);
		SocketServer.setCloseDebug(closeDebug);
		SocketServer.setSessionDelay(sessionDelay);
		SocketSessionResolver.setResolver(bufferResolver, serverResolver, beat, ok, failed);
	}

	/**
	 * 
	 */
	@Started
	protected void started() {
		if (open) {
			socketServer = new SocketServer();
			try {
				socketServer.start(acceptTimeout, idleTimeout, port, backlog, InetAddress.getByName(ip), bufferSize,
						receiveBufferSize, sendBufferSize, bufferResolver, sessionResolver);

			} catch (Exception e) {
				LOGGER.error("start opition server", e);
			}
		}
	}

	/**
	 * 
	 */
	@Stopping
	protected void stopping() {
		if (socketServer != null) {
			socketServer.close();
		}
	}
}
