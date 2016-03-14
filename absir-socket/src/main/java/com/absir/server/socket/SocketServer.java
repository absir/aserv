/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:02:53
 */
package com.absir.server.socket;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.util.UtilContext;
import com.absir.server.socket.resolver.IBufferResolver;
import com.absir.server.socket.resolver.ISessionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author absir
 */
public class SocketServer {

    /**
     * LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
    /**
     * acceptDebug
     */
    protected static boolean acceptDebug;
    /**
     * closeDebug
     */
    protected static boolean closeDebug;
    /**
     * sessionDelay
     */
    private static long sessionDelay = 5000;
    /**
     * globalSelSessionMap
     */
    private static ConcurrentHashMap<SocketChannel, SelSession> globalSelSessionMap;
    /**
     * keysField
     */
    private static Field keysField;
    /**
     * selectionKeyOpt
     */
    private static int selectionKeyOpt;
    /**
     * maxAcceptTime
     */
    protected long maxAcceptTime;
    /**
     * maxIdleTime
     */
    protected long maxIdleTime;
    /**
     * port
     */
    protected int port;
    /**
     * serverSocket
     */
    protected ServerSocketChannel serverSocketChannel;
    /**
     * serverSelector
     */
    protected Selector serverSelector;
    /**
     * socketBufferResolver
     */
    protected IBufferResolver socketBufferResolver;
    /**
     * socketSessionResolver
     */
    protected ISessionResolver socketSessionResolver;

    /**
     * @return the sessionDelay
     */
    public static final long getSessionDelay() {
        return sessionDelay;
    }

    /**
     * @param sessionDelay the sessionDelay to set
     */
    public static void setSessionDelay(long sessionDelay) {
        if (sessionDelay < 1000) {
            sessionDelay = 1000;
        }

        SocketServer.sessionDelay = sessionDelay;
    }

    /**
     *
     */
    public static synchronized void startSelSessionMap() {
        if (globalSelSessionMap == null) {
            globalSelSessionMap = new ConcurrentHashMap<SocketChannel, SelSession>();
            Thread globalSelectorThread = new Thread() {

                /*
                 * (non-Javadoc)
                 *
                 * @see java.lang.Thread#run()
                 */
                @Override
                public void run() {
                    try {
                        Iterator<Entry<SocketChannel, SelSession>> iterator;
                        Entry<SocketChannel, SelSession> entry;
                        SelSession selSession;
                        SocketChannel socketChannel;
                        long contextTime;
                        while (Environment.isStarted()) {
                            Thread.sleep(sessionDelay);
                            contextTime = UtilContext.getCurrentTime();
                            iterator = globalSelSessionMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                entry = iterator.next();
                                selSession = entry.getValue();
                                socketChannel = entry.getKey();
                                try {
                                    if (socketChannel.isConnected()) {
                                        if (selSession.getNextIdleTime() > contextTime) {
                                            continue;

                                        } else {
                                            selSession.getSocketServer().socketSessionResolver.idle(socketChannel,
                                                    selSession, contextTime);
                                            if (selSession.getNextIdleTime() > contextTime) {
                                                continue;
                                            }
                                        }
                                    }

                                    iterator.remove();
                                    sessionClose(selSession, socketChannel);

                                } catch (Throwable e) {
                                    if (Environment.getEnvironment() == Environment.DEVELOP) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            iterator = null;
                            entry = null;
                            selSession = null;
                            socketChannel = null;
                        }

                    } catch (InterruptedException e) {
                    }
                }
            };

            globalSelectorThread.setName("SocketServer.globalSelectorThread");
            globalSelectorThread.setDaemon(true);
            globalSelectorThread.start();
        }
    }

    /**
     * @param socketChannel
     * @return
     */
    public static SelSession forSession(SocketChannel socketChannel) {
        return globalSelSessionMap.get(socketChannel);
    }

    /**
     * @param selSession
     * @throws Throwable
     */
    public static void sessionClose(SelSession selSession) throws Throwable {
        sessionClose(selSession, selSession.getSocketChannel());
    }

    /**
     * @param selSession
     * @param socketChannel
     * @throws Throwable
     */
    public static void sessionClose(SelSession selSession, SocketChannel socketChannel) throws Throwable {
        try {
            socketChannel.close();

        } catch (Exception e) {
        }

        if (selSession != null) {
            synchronized (selSession) {
                selSession.nextIdleTime = 0;
                SocketBuffer socketBuffer = selSession.getSocketBuffer();
                if (socketBuffer != null) {
                    Serializable id = socketBuffer.getId();
                    if (id != null && id != SelSession.UN_REGISTER_ID) {
                        socketBuffer.setId(SelSession.UN_REGISTER_ID);
                        socketBuffer.close();
                        selSession.getSocketServer().socketSessionResolver.unRegister(id, socketChannel, selSession);
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public static Collection<SelSession> allSelSession() {
        return globalSelSessionMap.values();
    }

    /**
     * @param server
     */
    public static void closeAllSocketChannelServer(SocketServer server) {
        Entry<SocketChannel, SelSession> entry;
        SelSession selSession;
        SocketChannel socketChannel;
        Iterator<Entry<SocketChannel, SelSession>> iterator = globalSelSessionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            selSession = entry.getValue();
            if (selSession.getSocketServer() == server) {
                socketChannel = entry.getKey();
                try {
                    iterator.remove();
                    sessionClose(selSession, socketChannel);

                } catch (Throwable e) {
                    if (Environment.getEnvironment() == Environment.DEVELOP) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @return the acceptDebug
     */
    public static final boolean isAcceptDebug() {
        return acceptDebug;
    }

    /**
     * @param acceptDebug the acceptDebug to set
     */
    public static void setAcceptDebug(boolean acceptDebug) {
        SocketServer.acceptDebug = acceptDebug;
    }

    /**
     * @return the closeDebug
     */
    public static final boolean isCloseDebug() {
        return closeDebug;
    }

    /**
     * @param closeDebug the closeDebug to set
     */
    public static void setCloseDebug(boolean closeDebug) {
        SocketServer.closeDebug = closeDebug;
    }

    /**
     * @param socketChannel
     */
    public static void close(SocketChannel socketChannel) {
        try {
            if (globalSelSessionMap != null && socketChannel.isRegistered()) {
                SelSession selSession = globalSelSessionMap.remove(socketChannel);
                if (selSession != null) {
                    sessionClose(selSession, socketChannel);
                }
            }

            socketChannel.close();

        } catch (Throwable e) {
        }

        if (closeDebug) {
            new Exception().printStackTrace();
        }
    }

    /**
     * @param selSession
     * @param socketChannel
     */
    public static void close(SelSession selSession, SocketChannel socketChannel) {
        if (selSession == null) {
            close(socketChannel);

        } else {
            try {
                sessionClose(selSession, socketChannel);

            } catch (Throwable e) {
            }

            if (closeDebug) {
                new Exception().printStackTrace();
            }
        }
    }

    /**
     * @param socketChannel
     * @return
     */
    public static SelectionKey selectionKey(SocketChannel socketChannel) {
        SelectionKey[] keys = selectionKeys(socketChannel);
        return keys == null || keys.length == 0 ? null : keys[0];
    }

    /**
     * @param socketChannel
     * @return
     */
    public static SelectionKey[] selectionKeys(SocketChannel socketChannel) {
        if (socketChannel.isRegistered()) {
            if (selectionKeyOpt < 0) {
                return null;
            }

            if (selectionKeyOpt == 0) {
                keysField = KernelReflect.declaredField(SocketChannel.class, "keys");
                if (keysField == null) {
                    selectionKeyOpt = -1;
                    return null;

                } else {
                    selectionKeyOpt = 1;
                }
            }

            return (SelectionKey[]) KernelReflect.get(socketChannel, keysField);
        }

        return null;
    }

    /**
     * @param resolver
     * @param socketChannel
     * @param headerLength
     * @param headerBytes
     * @param bytes
     * @param offset
     * @param length
     * @return
     */
    public static byte[] getWriteByteBuffer(IBufferResolver resolver, SocketChannel socketChannel, int headerLength,
                                            byte[] headerBytes, byte[] bytes, int offset, int length) {
        byte[] headers = resolver.createByteHeader(headerLength);
        ByteBuffer byteBuffer = resolver.createByteBuffer(socketChannel, headerLength, headerBytes, bytes, offset,
                length);
        byte[] bodys = byteBuffer.array();
        int bOffset = byteBuffer.position();
        int bLimit = byteBuffer.limit();
        int hLength;
        if (headers == null) {
            if (bOffset == 0 && bLimit == bodys.length) {
                return bodys;
            }

            hLength = 0;

        } else {
            hLength = headers.length;
        }

        bLimit -= bOffset;
        length = hLength + bLimit;
        bytes = new byte[length];
        if (hLength > 0) {
            System.arraycopy(headers, 0, bytes, 0, hLength);
        }

        System.arraycopy(bodys, bOffset, bytes, hLength, bLimit);
        return bytes;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the serverSocketChannel
     */
    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    /**
     * @return
     */
    public boolean isClosed() {
        return serverSelector == null;
    }

    /**
     * @return the maxAcceptTime
     */
    public long getMaxAcceptTime() {
        return maxAcceptTime;
    }

    /**
     * @return the maxIdleTime
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * @return the socketBufferResolver
     */
    public IBufferResolver getSocketBufferResolver() {
        return socketBufferResolver;
    }

    /**
     * @return the socketSessionResolver
     */
    public ISessionResolver getSocketSessionResolver() {
        return socketSessionResolver;
    }

    /**
     * 开始服务
     *
     * @param acceptTimeout
     * @param idleTimeout
     * @param port
     * @param backlog
     * @param inetAddress
     * @param bufferSize
     * @param receiveBufferSize
     * @param sendBufferSize
     * @param bufferResolver
     * @param sessionResolver
     * @return
     * @throws IOException
     */
    public synchronized boolean start(long acceptTimeout, long idleTimeout, int port, int backlog,
                                      InetAddress inetAddress, final int bufferSize, int receiveBufferSize, final int sendBufferSize,
                                      IBufferResolver bufferResolver, ISessionResolver sessionResolver) throws IOException {
        maxAcceptTime = acceptTimeout;
        maxIdleTime = idleTimeout;
        if (serverSocketChannel != null) {
            return false;
        }

        if (globalSelSessionMap == null) {
            startSelSessionMap();
        }

        this.port = port;
        socketBufferResolver = bufferResolver;
        socketSessionResolver = sessionResolver;
        LOGGER.info("start socket " + inetAddress + " port " + port);
        // 初始化监听服务
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().setReceiveBufferSize(receiveBufferSize);
        serverSocketChannel.socket().bind(new InetSocketAddress(inetAddress, port), backlog);

        // 接受请求
        serverSelector = Selector.open();
        serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
        UtilContext.getThreadPoolExecutor().execute(new Runnable() {

            @Override
            public void run() {
                Selector selector = serverSelector;
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                while (selector == serverSelector && !Thread.interrupted()) {
                    try {
                        selector.select();
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (key.isAcceptable()) {
                                // 接受请求
                                SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                                if (!UtilContext.isWarnIdlePool()) {
                                    socketChannel.configureBlocking(false);
                                    socketChannel.socket().setSendBufferSize(sendBufferSize);
                                    try {
                                        // 处理注册请求
                                        long acceptTimeout = socketSessionResolver.acceptTimeout(socketChannel);
                                        if (acceptTimeout > 0) {
                                            if (acceptDebug) {
                                                LOGGER.debug(SocketServer.this + " accept : " + socketChannel);
                                            }

                                            SelSession selSession = new SelSession(SocketServer.this, socketChannel);
                                            selSession.retainIdleTimeout(acceptTimeout);
                                            globalSelSessionMap.put(socketChannel, selSession);
                                            socketChannel.register(selector, SelectionKey.OP_READ, selSession);
                                            continue;
                                        }

                                    } catch (Throwable e) {
                                        if (closeDebug) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                close(socketChannel);

                            } else {
                                // 处理数据
                                SelSession selSession = (SelSession) key.attachment();
                                SocketBuffer socketBuffer = selSession.getSocketBuffer();
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                try {
                                    buffer.clear();
                                    int length = socketChannel.read(buffer);
                                    if (length > 0) {
                                        if (socketBuffer == null) {
                                            socketBuffer = socketBufferResolver.createSocketBuff();
                                            selSession.setSocketBuffer(socketBuffer);
                                        }

                                        byte[] array = buffer.array();
                                        int position = 0;
                                        while (position < length) {
                                            position = socketBufferResolver.readByteBuffer(socketBuffer, array,
                                                    position, length);
                                            if (socketBuffer.getBuff() != null
                                                    && socketBuffer.getLength() <= socketBuffer.getBuffLengthIndex()) {
                                                if (socketBuffer.getId() == null) {
                                                    socketSessionResolver.register(socketChannel, selSession);
                                                    Serializable id = socketBuffer.getId();
                                                    if (id == null) {
                                                        break;
                                                    }

                                                } else {
                                                    socketSessionResolver.receiveByteBuffer(socketChannel, selSession);
                                                }

                                                socketBufferResolver.readByteBufferDone(socketBuffer);
                                            }

                                            position++;
                                        }

                                        continue;
                                    }

                                } catch (Throwable e) {
                                    if (closeDebug) {
                                        e.printStackTrace();
                                    }
                                }

                                // 注销请求
                                key.cancel();
                                try {
                                    if (socketBuffer != null) {
                                        globalSelSessionMap.remove(socketChannel);
                                        sessionClose(selSession, socketChannel);
                                    }

                                } catch (Throwable e) {
                                    if (closeDebug) {
                                        e.printStackTrace();
                                    }
                                }

                                close(socketChannel);
                            }
                        }

                    } catch (Throwable e) {
                        if (closeDebug) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    selector.close();

                } catch (Throwable e) {
                }

                close();
            }

        });

        return true;
    }

    /**
     * 关闭服务
     */
    public synchronized void close() {
        if (serverSocketChannel != null) {
            if (serverSelector != null) {
                try {
                    serverSelector.close();
                } catch (Throwable e) {
                }

                serverSelector = null;
            }

            closeAllSocketChannelServer(SocketServer.this);
            try {
                LOGGER.info("stop socket port " + port + " => " + serverSocketChannel.socket().getLocalPort());
                serverSocketChannel.close();

            } catch (Throwable e) {
            }

            serverSocketChannel = null;
        }
    }
}
