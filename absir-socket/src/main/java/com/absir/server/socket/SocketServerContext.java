/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-18 下午7:26:21
 */
package com.absir.server.socket;

import com.absir.server.socket.SocketServerContext.ChannelContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServerContext<T extends ChannelContext> {

    protected boolean mult;

    @JsonIgnore
    protected ConcurrentHashMap<Serializable, T> channelContexts = createContexts();

    @JsonIgnore
    protected ConcurrentHashMap<SocketChannel, T> channelContextMults;

    public boolean isMult() {
        return mult;
    }

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

    protected ConcurrentHashMap<Serializable, T> createContexts() {
        return new ConcurrentHashMap<Serializable, T>();
    }

    protected ConcurrentHashMap<SocketChannel, T> createContextMults() {
        return new ConcurrentHashMap<SocketChannel, T>();
    }

    public long getOnline() {
        return channelContexts.size() + (channelContextMults == null ? 0 : channelContextMults.size());
    }

    public Map<Serializable, T> getChannelContexts() {
        return channelContexts;
    }

    public Map<SocketChannel, T> getChannelContextMults() {
        return channelContextMults;
    }

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

    public static class ChannelContext {

        protected SocketChannel channel;

        protected Object attachObj;

        public ChannelContext(SocketChannel channel) {
            this.channel = channel;
        }

        public SocketChannel getChannel() {
            return channel;
        }

        public Object getAttachObj() {
            return attachObj;
        }

        public void setAttachObj(Object attachObj) {
            this.attachObj = attachObj;
        }
    }

}
