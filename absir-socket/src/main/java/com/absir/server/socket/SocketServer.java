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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    protected static boolean acceptDebug;

    protected static boolean closeDebug;

    private static long sessionDelay = 5000;

    private static ConcurrentHashMap<SocketChannel, SelSession> globalSelSessionMap;

    private static Field keysField;

    private static int selectionKeyOpt;

    protected long maxAcceptTime;

    protected long maxIdleTime;

    protected int port;

    protected ServerSocketChannel serverSocketChannel;

    protected Selector serverSelector;

    protected IBufferResolver socketBufferResolver;

    protected ISessionResolver socketSessionResolver;

    public static final long getSessionDelay() {
        return sessionDelay;
    }

    public static void setSessionDelay(long sessionDelay) {
        if (sessionDelay < 1000) {
            sessionDelay = 1000;
        }

        SocketServer.sessionDelay = sessionDelay;
    }

    public static synchronized void startSelSessionMap() {
        if (globalSelSessionMap == null) {
            globalSelSessionMap = new ConcurrentHashMap<SocketChannel, SelSession>();
            Thread globalSelectorThread = new Thread() {

                @Override
                public void run() {
                    try {
                        Iterator<Entry<SocketChannel, SelSession>> iterator;
                        Entry<SocketChannel, SelSession> entry;
                        SelSession selSession;
                        SocketChannel socketChannel;
                        long currentTime;
                        while (Environment.isStarted()) {
                            Thread.sleep(sessionDelay);
                            currentTime = UtilContext.getCurrentTime();
                            iterator = globalSelSessionMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                entry = iterator.next();
                                selSession = entry.getValue();
                                socketChannel = entry.getKey();
                                try {
                                    if (socketChannel.isConnected()) {
                                        if (selSession.getNextIdleTime() > currentTime) {
                                            continue;

                                        } else {
                                            selSession.getSocketServer().socketSessionResolver.idle(socketChannel,
                                                    selSession, currentTime);
                                            if (selSession.getNextIdleTime() > currentTime) {
                                                continue;
                                            }
                                        }
                                    }

                                    iterator.remove();
                                    sessionClose(selSession, socketChannel);

                                } catch (Throwable e) {
                                    Environment.throwable(e);
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

    public static SelSession forSession(SocketChannel socketChannel) {
        return globalSelSessionMap.get(socketChannel);
    }

    protected static void sessionClose(SelSession selSession, SocketChannel socketChannel) throws Throwable {
        if (socketChannel != null) {
            try {
                socketChannel.close();

            } catch (Exception e) {
            }
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

    public static Collection<SelSession> allSelSession() {
        return globalSelSessionMap.values();
    }

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
                    Environment.throwable(e);
                }
            }
        }
    }

    public static final boolean isAcceptDebug() {
        return acceptDebug;
    }

    public static void setAcceptDebug(boolean acceptDebug) {
        SocketServer.acceptDebug = acceptDebug;
    }

    public static final boolean isCloseDebug() {
        return closeDebug;
    }

    public static void setCloseDebug(boolean closeDebug) {
        SocketServer.closeDebug = closeDebug;
    }

    public static void closeSelector(Selector selector) {
        try {
            selector.close();

        } catch (Throwable e) {
            LOGGER.error("Close selector error.", e);
        }
    }

    public static void close(SocketChannel socketChannel) {
        close(null, socketChannel);
    }

    public static void close(SelSession selSession, SocketChannel socketChannel) {
        if (socketChannel == null) {
            if (selSession == null) {
                return;
            }

            socketChannel = selSession.getSocketChannel();
        }

        try {
            if (globalSelSessionMap != null && socketChannel.isRegistered()) {
                SelSession session = globalSelSessionMap.remove(socketChannel);
                if (selSession == null) {
                    selSession = session;
                }
            }

            socketChannel.close();

        } catch (Throwable e) {
            LOGGER.error("Close socketChannel error.", e);
        }

        try {
            sessionClose(selSession, socketChannel);

        } catch (Throwable e) {
            LOGGER.error("Close sessionClose error.", e);
        }

        if (closeDebug) {
            new Exception().printStackTrace();
        }
    }

    public static SelectionKey selectionKey(SocketChannel socketChannel) {
        SelectionKey[] keys = selectionKeys(socketChannel);
        return keys == null || keys.length == 0 ? null : keys[0];
    }

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

    public static byte[] getWriteByteBuffer(IBufferResolver resolver, SocketChannel socketChannel, int headerLength,
                                            byte[] headerBytes, byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = resolver.createByteBuffer(socketChannel, headerLength, bytes, offset,
                length);
        byte[] headers = resolver.createByteHeader(headerLength, byteBuffer);
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

    public int getPort() {
        return port;
    }

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public boolean isClosed() {
        return serverSelector == null;
    }

    public long getMaxAcceptTime() {
        return maxAcceptTime;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public IBufferResolver getSocketBufferResolver() {
        return socketBufferResolver;
    }

    public ISessionResolver getSocketSessionResolver() {
        return socketSessionResolver;
    }

    private static final long __JVMBUG_CHECK = 10;

    private static final long __JVMBUG_PERIOD = 1000;

    private static final int __JVMBUG_THRESHHOLD = 512;

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
    public synchronized boolean start(final long acceptTimeout, long idleTimeout, int port, int backlog,
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
                int _jvmBugs = 0;
                long _jvmSelectStart = 0;
                while (selector == serverSelector && !Thread.interrupted() && Environment.isActive()) {
                    try {
                        if (_jvmBugs > __JVMBUG_CHECK) {
                            _jvmSelectStart = System.currentTimeMillis();
                        }

                        if (selector.select() == 0) {
                            if (_jvmBugs > __JVMBUG_CHECK) {
                                _jvmSelectStart = System.currentTimeMillis() - _jvmSelectStart;
                                if (_jvmSelectStart > __JVMBUG_THRESHHOLD) {
                                    _jvmBugs = 0;
                                }
                            }

                            if (++_jvmBugs > __JVMBUG_PERIOD) {
                                synchronized (SocketServer.this) {
                                    if (selector == serverSelector) {
                                        rebuildSelector();
                                        selector = serverSelector;
                                        selector.selectNow();
                                        _jvmBugs = 0;
                                        continue;
                                    }
                                }
                            }

                        } else {
                            _jvmBugs = 0;
                        }

                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (!Environment.isActive()) {
                                break;
                            }

                            if (key.isAcceptable()) {
                                // 接受请求
                                SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                                if (!UtilContext.isWarnIdlePool()) {
                                    socketChannel.configureBlocking(false);
                                    socketChannel.socket().setSendBufferSize(sendBufferSize);
                                    try {
                                        // 处理注册请求
                                        long timeout = socketSessionResolver.acceptTimeout(socketChannel);
                                        if (timeout == 0) {
                                            timeout = acceptTimeout;
                                        }

                                        if (timeout > 0) {
                                            if (acceptDebug) {
                                                LOGGER.debug(SocketServer.this + " accept : " + socketChannel);
                                            }

                                            SelSession selSession = new SelSession(SocketServer.this, socketChannel);
                                            selSession.retainIdleTimeout(timeout);
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
                                            position = socketBufferResolver.readByteBuffer(selSession, socketBuffer, array, position, length);
                                            if (socketBuffer.getBuff() != null && socketBuffer.getLength() <= socketBuffer.getBuffLengthIndex()) {
                                                //SocketAdapter._debugInfo("SocketServer readByteBuffer  <= " + Arrays.toString(socketBuffer.getBuff()));
                                                if (socketBuffer.getId() == null) {
                                                    socketSessionResolver.register(socketChannel, selSession);
                                                    Serializable id = socketBuffer.getId();
                                                    if (id == null) {
                                                        break;
                                                    }

                                                } else {
                                                    selSession.retainIdleTimeout();
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
                                        close(selSession, socketChannel);
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

                closeSelector(selector);
                close();
            }

        });

        return true;
    }

    private synchronized void rebuildSelector() {
        if (serverSelector != null) {
            Selector newSelector = null;
            try {
                newSelector = Selector.open();

            } catch (IOException e) {
                LOGGER.error("Fail open a new register", e);
            }

            while (true) {
                try {
                    for (SelectionKey key : serverSelector.keys()) {
                        try {
                            if (key.channel().keyFor(newSelector) != null) {
                                continue;
                            }

                            int interestOps = key.interestOps();
                            key.cancel();
                            key.channel().register(newSelector, interestOps, key.attachment());

                        } catch (Exception e) {
                            LOGGER.error("Fail to register a channel to newSelector", e);
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            key.cancel();
                            close(socketChannel);
                        }
                    }

                } catch (ConcurrentModificationException e) {
                    continue;
                }

                break;
            }

            closeSelector(serverSelector);
            serverSelector = newSelector;
        }
    }

    /**
     * 关闭服务
     */
    public synchronized void close() {
        if (serverSocketChannel != null) {
            if (serverSelector != null) {
                closeSelector(serverSelector);
                serverSelector = null;
            }

            closeAllSocketChannelServer(SocketServer.this);
            try {
                LOGGER.info("stop socket port " + port + " => " + serverSocketChannel.socket().getLocalPort());
                serverSocketChannel.close();

            } catch (Throwable e) {
                LOGGER.error("Close serverSocketChannel error.", e);
            }

            serverSocketChannel = null;
        }
    }
}
