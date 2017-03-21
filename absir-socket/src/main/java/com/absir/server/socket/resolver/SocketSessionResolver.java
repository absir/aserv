/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月8日 下午4:29:50
 */
package com.absir.server.socket.resolver;

import com.absir.client.ServerEnvironment;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilContext.RunnableGuarantee;
import com.absir.server.socket.SelSession;
import com.absir.server.socket.SocketBuffer;
import com.absir.server.socket.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class SocketSessionResolver implements ISessionResolver {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketSessionResolver.class);

    protected static IBufferResolver bufferResolver;

    protected static IServerResolver serverResolver;

    protected static byte[] beat;

    protected static byte[] beatBuffer;

    protected static byte[] ok;

    protected static long adapterTime;

    protected static byte[] _okBuffer;

    protected static byte[] failedBuffer;

    protected PropertyFilter propertyFilter;

    public static byte[] getBeat() {
        return beat;
    }

    public static byte[] getBeatBuffer() {
        return beatBuffer;
    }

    public static byte[] getOk() {
        return ok;
    }

    public static byte[] getOkBuffer() {
        if (_okBuffer == null || adapterTime != ServerEnvironment.getStartTime()) {
            byte[] ok = (new String(SocketSessionResolver.ok) + ',' + System.currentTimeMillis()).getBytes();
            _okBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, ok, 0, ok.length);
        }

        return _okBuffer;
    }

    public static byte[] getFailedBuffer() {
        return failedBuffer;
    }

    public static void setResolver(IBufferResolver bufferResolver, IServerResolver serverResolver, byte[] beat,
                                   byte[] ok, byte[] failed) {
        SocketSessionResolver.bufferResolver = bufferResolver;
        SocketSessionResolver.serverResolver = serverResolver;
        SocketSessionResolver.beat = beat;
        beatBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, beat, 0, beat.length);
        SocketSessionResolver.ok = ok;
        failedBuffer = SocketBufferResolver.createByteBufferFull(bufferResolver, null, 0, failed, 0, failed.length);
    }

    public IBufferResolver getBufferResolver() {
        return bufferResolver;
    }

    public IServerResolver getServerResolver() {
        return serverResolver;
    }

    protected boolean writeSuccess(SelSession selSession, SocketChannel socketChannel, Serializable id) {
        return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, getOkBuffer());
    }

    protected boolean writeFailed(SelSession selSession, SocketChannel socketChannel) {
        return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, failedBuffer);
    }

    protected boolean writeBeat(SelSession selSession, SocketChannel socketChannel, Serializable id) {
        return SocketBufferResolver.writeBufferTimeout(selSession, socketChannel, beatBuffer);
    }

    public void setSessionFilters(String[] includes, String[] excludes) {
        PropertyFilter filter = null;
        if (includes != null && includes.length > 0) {
            filter = new PropertyFilter();
            for (String include : includes) {
                filter.include(include);
            }
        }

        if (excludes != null && excludes.length > 0) {
            if (filter == null) {
                filter = new PropertyFilter();
            }

            for (String exclude : excludes) {
                filter.include(exclude);
            }
        }

        propertyFilter = filter;
    }

    @Override
    public long acceptTimeout(SocketChannel socketChannel) throws Throwable {
        if (propertyFilter == null || propertyFilter.isMatchPath(socketChannel.socket().getInetAddress().toString())) {
            return getServerResolver().acceptTimeoutNIO(socketChannel);
        }

        return -1;
    }

    @Override
    public void idle(final SocketChannel socketChannel, final SelSession selSession, final long contextTime) {
        selSession.retainIdleTimeout();
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                if (writeBeat(selSession, socketChannel, beatBuffer)) {
                    getServerResolver().doBeat(socketChannel, selSession, contextTime);
                }
            }
        });
    }

    @Override
    public void register(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
        final SocketBuffer socketBuffer = selSession.getSocketBuffer();
        socketBuffer.setId(SelSession.UN_REGISTER_ID);
        final byte[] buffer = socketBuffer.getBuff();
        socketBuffer.setBuff(null);
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                Serializable id = null;
                try {
                    getServerResolver().register(socketChannel, selSession, buffer, System.currentTimeMillis());
                    id = socketBuffer.getId();
                    if (id == SelSession.UN_REGISTER_ID) {
                        id = null;
                        socketBuffer.setId(null);
                    }

                    if (id == null) {
                        if (writeFailed(selSession, socketChannel)) {
                            selSession.retainIdleTimeout();
                        }

                    } else {
                        if (writeSuccess(selSession, socketChannel, id)) {
                            selSession.retainIdleTimeout();
                        }
                    }

                } catch (Throwable e) {
                    if (SocketServer.isCloseDebug()) {
                        e.printStackTrace();
                    }

                } finally {
                    if (!(id == null || socketChannel.isConnected())) {
                        SocketServer.close(selSession, socketChannel);
                    }
                }
            }
        });
    }

    protected boolean doDenied(SocketChannel socketChannel, Serializable id, byte[] buffer, SocketBuffer socketBuffer) {
        return id == null || id == SelSession.UN_REGISTER_ID;
    }

    protected boolean isBeat(SocketChannel socketChannel, SocketBuffer socketBuffer, byte[] buffer) {
        int length = beat.length;
        if (buffer.length == length) {
            for (int i = 0; i < length; i++) {
                if (buffer[i] != beat[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void receiveByteBuffer(final SocketChannel socketChannel, final SelSession selSession) throws Throwable {
        final SocketBuffer socketBuffer = selSession.getSocketBuffer();
        final Serializable id = socketBuffer.getId();
        final byte[] buffer = socketBuffer.getBuff();
        socketBuffer.setBuff(null);
        if (doDenied(socketChannel, id, buffer, socketBuffer)) {
            return;
        }

        final long currentTime = System.currentTimeMillis();
        if (isBeat(socketChannel, socketBuffer, buffer)) {
            getServerResolver().receiveBeatNIO(socketChannel, selSession, currentTime);
            return;
        }

        if (getServerResolver().receiveBufferNIO(socketChannel, selSession, socketBuffer, buffer, currentTime)) {
            return;
        }

        if (socketBuffer.addBufferQueue(buffer)) {
            return;
        }

        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                byte[] queueBuffer = buffer;
                while (queueBuffer != null) {
                    try {
                        getServerResolver().receiveByteBuffer(socketChannel, selSession, socketBuffer, queueBuffer, currentTime);

                    } catch (Throwable e) {
                        LOGGER.error("receiveByteBuffer", e);
                    }

                    if (!socketChannel.isConnected()) {
                        break;
                    }

                    queueBuffer = socketBuffer.readBufferQueue();
                }
            }
        });
    }

    @Override
    public void unRegister(final Serializable id, final SocketChannel socketChannel, final SelSession selSession)
            throws Throwable {
        final long currentTime = System.currentTimeMillis();
        UtilContext.getThreadPoolExecutor().execute(new RunnableGuarantee() {

            @Override
            public void run() {
                getServerResolver().unRegister(id, socketChannel, selSession, currentTime);
            }
        });
    }
}
