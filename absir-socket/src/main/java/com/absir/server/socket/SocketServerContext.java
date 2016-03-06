/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-3-18 下午7:26:21
 */
package com.absir.server.socket;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.absir.server.socket.SocketServerContext.ChannelContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author absir
 * 
 */
public class SocketServerContext<T extends ChannelContext> {

	/** mutil */
	protected boolean mult;

	/** channelContexts */
	@JsonIgnore
	protected ConcurrentHashMap<Serializable, T> channelContexts = createContexts();

	/** channelContextMults */
	@JsonIgnore
	protected ConcurrentHashMap<SocketChannel, T> channelContextMults;

	/**
	 * @author absir
	 *
	 */
	public static class ChannelContext {

		/** channel */
		protected SocketChannel channel;

		/** attachObj */
		protected Object attachObj;

		/**
		 * @param channel
		 */
		public ChannelContext(SocketChannel channel) {
			this.channel = channel;
		}

		/**
		 * @return the channel
		 */
		public SocketChannel getChannel() {
			return channel;
		}

		/**
		 * @return the attachObj
		 */
		public Object getAttachObj() {
			return attachObj;
		}

		/**
		 * @param attachObj
		 *            the attachObj to set
		 */
		public void setAttachObj(Object attachObj) {
			this.attachObj = attachObj;
		}
	}

	/**
	 * @return the mult
	 */
	public boolean isMult() {
		return mult;
	}

	/**
	 * @return
	 */
	protected ConcurrentHashMap<Serializable, T> createContexts() {
		return new ConcurrentHashMap<Serializable, T>();
	}

	/**
	 * @return
	 */
	protected ConcurrentHashMap<SocketChannel, T> createContextMults() {
		return new ConcurrentHashMap<SocketChannel, T>();
	}

	/**
	 * @param mult
	 *            the mult to set
	 */
	public void setMult(boolean mult) {
		this.mult = mult;
		if (mult) {
			if (channelContextMults == null) {
				channelContextMults = createContextMults();
			}

		} else {
			channelContextMults = null;
		}
	}

	/**
	 * @return the online
	 */
	public long getOnline() {
		return channelContexts.size() + (channelContextMults == null ? 0 : channelContextMults.size());
	}

	/**
	 * @return the channelContexts
	 */
	public Map<Serializable, T> getChannelContexts() {
		return channelContexts;
	}

	/**
	 * @return the channelContextMults
	 */
	public Map<SocketChannel, T> getChannelContextMults() {
		return channelContextMults;
	}

	/**
	 * @param id
	 * @param channelContext
	 */
	public synchronized void loginSocketChannel(Serializable id, T channelContext) {
		ChannelContext context = channelContexts.put(id, channelContext);
		if (context != null) {
			if (mult) {
				channelContextMults.put(channelContext.channel, channelContext);

			} else {
				SocketServer.close(channelContext.channel);
			}
		}
	}

	/**
	 * @param id
	 * @param channel
	 * @return
	 */
	public synchronized T logoutSocketChannel(Serializable id, SocketChannel channel) {
		if (mult) {
			T context = channelContexts.get(id);
			if (context == null || context.channel != channel) {
				return channelContextMults.remove(channel);

			} else {
				return channelContexts.remove(id);
			}

		} else {
			T context = channelContexts.remove(id);
			if (context != null && context.channel != channel) {
				channelContexts.put(id, context);
				context = null;
			}

			return context;
		}
	}

}
