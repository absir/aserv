/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月8日 下午4:57:44
 */
package com.absir.master;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.master.resolver.MasterBufferResolver;
import com.absir.master.resolver.MasterSessionResolver;
import com.absir.server.socket.SocketServer;
import com.absir.server.socket.SocketServerContext;
import com.absir.server.socket.SocketServerContext.ChannelContext;

/**
 * @author absir
 *
 */
@Base
@Bean
public class InputMasterContext {

	/** ME */
	public static final InputMasterContext ME = BeanFactoryUtils.get(InputMasterContext.class);

	/** acceptTimeout */
	@Value("master.accept.timeout")
	protected static long acceptTimeout = 120000;

	/** idleTimeout */
	@Value("master.idle.timeout")
	protected static long idleTimeout = 30000;

	/** ip */
	@Value("server.master.ip")
	protected String ip;

	/** port */
	@Value("server.master.port")
	protected int port = 28890;

	/** backlog */
	@Value("server.master.backlog")
	protected int backlog = 50;

	/** bufferSize */
	@Value("server.master.bufferSize")
	protected int bufferSize = 1024;

	/** bufferSize */
	@Value("server.master.receiveBufferSize")
	protected int receiveBufferSize = 2048;

	/** bufferSize */
	@Value("server.master.sendBufferSize")
	protected int sendBufferSize = 2048;

	/** hosts */
	@Value("server.master.host")
	protected String[] hosts = null;

	/** hosts */
	@Value("server.master.host.exclude")
	protected String[] excludes = null;

	@Value("server.master.key")
	protected String key = "absir@qq.com";

	/** socketServer */
	protected SocketServer socketServer;

	/** serverContext */
	private SocketServerContext<MasterChannelContext> serverContext = new SocketServerContext<MasterChannelContext>();

	/** sessionResolver */
	@Inject
	protected MasterSessionResolver sessionResolver;

	/**
	 * @author absir
	 *
	 */
	public static class MasterChannelContext extends ChannelContext {

		/** slaveKey */
		protected String slaveKey;

		/**
		 * @param channel
		 */
		public MasterChannelContext(SocketChannel channel) {
			super(channel);
		}

		/**
		 * @return the slaveKey
		 */
		public String getSlaveKey() {
			return slaveKey;
		}
	}

	/**
	 * @return the serverContext
	 */
	public SocketServerContext<MasterChannelContext> getServerContext() {
		return serverContext;
	}

	/**
	 * @return
	 */
	protected SocketServer createSocketServer() {
		return new SocketServer();
	}

	/**
	 * @return the sessionResolver
	 */
	public MasterSessionResolver getSessionResolver() {
		return sessionResolver;
	}

	/**
	 * 开始服务
	 * 
	 * @throws IOException
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

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param id
	 * @param secerets
	 * @param validate
	 * @param params
	 * @param socketChannel
	 */
	public void registerSlaveKey(String id, byte[] secerets, String validate, String[] params,
			SocketChannel socketChannel) {
		MasterChannelContext channelContext = new MasterChannelContext(socketChannel);
		channelContext.slaveKey = validate;
		serverContext.loginSocketChannel(id, channelContext);
	}

	/**
	 * @param id
	 * @param socketChannel
	 * @return
	 */
	public MasterChannelContext unregisterSlaveKey(String id, SocketChannel socketChannel) {
		return serverContext.logoutSocketChannel(id, socketChannel);
	}

	/**
	 * @param id
	 * @return
	 */
	public String getSlaveKey(Serializable id) {
		MasterChannelContext channelContext = serverContext.getChannelContexts().get(id);
		return channelContext == null ? null : channelContext.slaveKey;
	}

	/**
	 * @param id
	 * @return
	 */
	public SocketChannel getSlaveSocketChannel(Serializable id) {
		MasterChannelContext channelContext = serverContext.getChannelContexts().get(id);
		return channelContext == null ? null : channelContext.getChannel();
	}

}
