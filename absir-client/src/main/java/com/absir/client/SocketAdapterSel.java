/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月27日 下午7:20:15
 */
package com.absir.client;

import com.absir.core.base.Environment;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.core.util.UtilPipedStream.NextOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

public class SocketAdapterSel extends SocketAdapter {

    public static final long PIPED_STREAM_TIMEOUT = 30000;

    public static final int POST_BUFF_LEN = 1024;

    private static int buffSize = 1024;

    private static UtilAtom atom;

    private static Selector selector;

    private UtilPipedStream pipedStream;

    private UtilActivePool activePool;

    public static void setBuffSize(int size) {
        if (size < 16) {
            size = 16;
        }

        buffSize = size;
    }

    protected static Selector getAdapterSelector() {
        if (selector == null) {
            try {
                synchronized (SocketAdapterSel.class) {
                    if (selector == null) {
                        atom = new UtilAtom();
                        selector = Selector.open();
                        Thread thread = new Thread() {

                            public void run() {
                                byte[] array = new byte[buffSize];
                                ByteBuffer buffer = ByteBuffer.wrap(array);
                                while (Environment.isStarted()) {
                                    try {
                                        atom.await();
                                        selector.select();
                                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                                        while (iterator.hasNext()) {
                                            SelectionKey key = iterator.next();
                                            SocketAdapter socketAdapter = null;
                                            SocketChannel socketChannel = null;
                                            try {
                                                socketAdapter = (SocketAdapter) key.attachment();
                                                socketChannel = (SocketChannel) key.channel();
                                                buffer.clear();
                                                int length = socketChannel.read(buffer);
                                                if (length > 0) {
                                                    socketAdapter.receiveByteBuffer(socketChannel.socket(), array, 0, length);
                                                    continue;
                                                }

                                            } catch (Throwable e) {
                                                Environment.throwable(e);
                                            }

                                            key.cancel();
                                            if (socketAdapter != null && socketChannel != null) {
                                                final SocketAdapter adapter = socketAdapter;
                                                final Socket socket = socketChannel.socket();
                                                UtilContext.executeSecurity(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        adapter.disconnect(socket);
                                                    }
                                                });
                                            }
                                        }

                                    } catch (Throwable e) {
                                        Environment.throwable(e);
                                    }
                                }
                            }
                        };

                        thread.setDaemon(true);
                        thread.setName("SocketAdapter.Selector");
                        thread.start();
                    }
                }

            } catch (IOException e) {
                Environment.throwable(e);
            }
        }

        return selector;
    }

    public UtilPipedStream getPipedStream() {
        if (pipedStream == null) {
            pipedStream = new UtilPipedStream(PIPED_STREAM_TIMEOUT);
        }

        return pipedStream;
    }

    public UtilActivePool getActivePool() {
        if (activePool == null) {
            activePool = new UtilActivePool();
        }

        return activePool;
    }

    @Override
    public void close() {
        super.close();
        _debugInfo("SocketAdapterSel close");
        if (pipedStream != null) {
            pipedStream.close();
            pipedStream = null;
        }

        if (activePool != null) {
            activePool.clear();
        }
    }

    @Override
    public boolean sendDataReal(byte[] buffer, int offset, int length) {
        Socket socket = getSocket();
        if (socket != null) {
            SocketChannel socketChannel = getSocket().getChannel();
            if (socketChannel != null && !socketChannel.isBlocking()) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, offset, length);
                synchronized (socketChannel) {
                    try {
                        _debugInfo("SocketAdapterSel sendData => " + Arrays.toString(Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit())));
                        SocketNIO._debugInfo = true;
                        synchronized (socketChannel) {
                            SocketNIO.writeTimeout(socketChannel, byteBuffer);
                        }

                        return true;

                    } catch (Throwable e) {
                        Environment.throwable(e);
                    }
                }

                disconnect(socket);
                return false;
            }
        }

        return super.sendDataReal(buffer, offset, length);
    }

    protected NextOutputStream createNextOutputStream(int hashIndex) {
        return getPipedStream().createNextOutputStream(hashIndex);
    }

    @Override
    public void receiveCallback(int offset, byte[] buffer, byte flag, Integer callbackIndex) {
        if ((flag & STREAM_FLAG) != 0) {
            //没有POST_FLAG只管写入，有POST_FLAG才需要创建
            if ((flag & POST_FLAG) == 0) {
                int length = buffer.length;
                int streamIndex = getVarints(buffer, offset, length);
                int streamIndexLen = getVarintsLength(streamIndex);
                //if (streamIndexLen > 0) {
                int offLen = offset + streamIndexLen;
                // 写入流信息
                NextOutputStream outputStream = getPipedStream().getOutputStream(streamIndex);
                if (outputStream != null) {
                    try {
                        outputStream.write(buffer, offLen, length - offLen);
                        return;

                    } catch (Throwable e) {
                        Environment.throwable(e);
                        UtilPipedStream.closeCloseable(outputStream);
                    }
                }

                sendData(sendDataBytesReal(0, buffer, offset, offLen, true, false, STREAM_CLOSE_FLAG, 0, null, 0, 0, true));
                //}

                return;
            }

        } else if ((flag & STREAM_CLOSE_FLAG) != 0) {
            int length = buffer.length;
            int streamIndex = getVarints(buffer, offset, length);
            _debugInfo("SocketAdapterSel STREAM_CLOSE_FLAG " + streamIndex + " : " + ((flag & SocketAdapter.POST_FLAG) == 0));
            if ((flag & POST_FLAG) == 0) {
                //发送关闭
                if (activePool != null) {
                    getActivePool().remove(streamIndex);
                }

            } else {
                //接收关闭
                NextOutputStream outputStream = getPipedStream().getOutputStream(streamIndex);
                if (outputStream != null) {
                    try {
                        outputStream.close();

                    } catch (Throwable e) {
                        Environment.throwable(e);
                    }
                }
            }

            return;
        }

        super.receiveCallback(offset, buffer, flag, callbackIndex);
    }

    @Override
    public void receiveCallback(CallbackAdapter callbackAdapter, int offset, byte[] buffer, byte flag,
                                Integer callbackIndex) {
        if ((flag & STREAM_FLAG) != 0) {
            int length = buffer.length;
            int streamIndex = getVarints(buffer, offset, length);
            int streamIndexLen = getVarintsLength(streamIndex);
            int offLen = offset + streamIndexLen;
            try {
                NextOutputStream outputStream = callbackAdapter instanceof CallbackAdapterStream ? createNextOutputStream(streamIndex) : null;
                _debugInfo("SocketAdapterSel STREAM_FLAG open " + streamIndex + " : " + outputStream);
                if (outputStream == null) {
                    // 不是CallbackAdapterStream 不能接受流数据返回
                    sendData(sendDataBytesReal(0, buffer, offset, offLen, true, false, STREAM_CLOSE_FLAG, 0, null, 0, 0, true));

                } else {
                    // 生成PipedInputStream执行回调
                    CallbackAdapterStream callbackAdapterStream = (CallbackAdapterStream) callbackAdapter;
                    callbackAdapter = null;
                    callbackAdapterStream.doWith(this, offLen, buffer, outputStream);
                    return;
                }

            } catch (Exception e) {
                LOGGER.error("receiveCallbackStream", e);
            }

            if (callbackAdapter != null) {
                callbackAdapter.doWith(this, offset, null);
            }

            return;
        }

        super.receiveCallback(callbackAdapter, offset, buffer, flag, callbackIndex);
    }

    @Override
    public synchronized void receiveSocketChannelStart() {
        if (receiveStarted) {
            return;
        }

        SocketChannel socketChannel = getSocket().getChannel();
        if (socketChannel != null) {
            receiveStarted = true;
            try {
                clearReceiveBuff();
                registerSelector(socketChannel);

            } catch (Throwable e) {
                Environment.throwable(e);
            }
        }
    }

    protected void registerSelector(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);
        Selector selector = getAdapterSelector();
        try {
            atom.increment();
            selector.wakeup();
            socketChannel.register(selector, SelectionKey.OP_READ, this);

        } finally {
            atom.decrement();
        }
    }

    /**
     * @return make varints mode right set postBuffLen 128(127 VARINTS_1_LENGTH) ~ 10240(16383 VARINTS_2_LENGTH) - 32
     */
    protected int getPostBuffLen() {
        return POST_BUFF_LEN;
    }

    protected RegisteredRunnable sendStream(byte[] dataBytes, boolean human, final int callbackIndex,
                                            final InputStream inputStream, final Closeable pipeOutput, final CallbackTimeout callbackTimeout, final long timeout, final Runnable inputRunnable) {
        connect();
        int sended = 0;
        final UtilActivePool.ActiveTemplate template = getActivePool().addObject(pipeOutput == null ? inputStream : pipeOutput);
        if (template == null) {
            UtilPipedStream.closeCloseable(inputStream);
            UtilPipedStream.closeCloseable(pipeOutput);
            return null;
        }

        final int streamIndex = template.object;
        try {
            final int streamIndexLen = getVarintsLength(streamIndex);
            final byte[] buffer = sendDataBytes(streamIndexLen, dataBytes, true, human, (byte) (STREAM_FLAG | POST_FLAG), callbackIndex, null);
            int offLen = getVarintsLength(buffer, 0, buffer.length) + 1;
            setVarintsLength(buffer, offLen + getSendDataBytesHeaderLength(), streamIndex);
            final Runnable postRunnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        if (inputRunnable != null) {
                            boolean inputed = false;
                            try {
                                UtilContext.getThreadPoolExecutor().execute(inputRunnable);
                                inputed = true;

                            } finally {
                                if (!inputed) {
                                    UtilPipedStream.closeCloseable(inputStream);
                                    UtilPipedStream.closeCloseable(pipeOutput);
                                }
                            }
                        }

                        byte[] sendBuffer = sendDataBytesReal(streamIndexLen, null, 0, 0, true, false, STREAM_FLAG, 0, null, 0, getPostBuffLen(), true);
                        setVarintsLength(sendBuffer, 3, streamIndex);
                        int postOff = 3 + streamIndexLen;
                        int length = sendBuffer.length - postOff;
                        int len;
                        while ((len = inputStream.read(sendBuffer, postOff, length)) > 0) {
                            len += postOff - 2;
                            sendBuffer[0] = (byte) ((len & 0x7F) | 0x80);
                            sendBuffer[1] = (byte) ((len >> 7) & 0x7F);

                            if (template.object == null || !sendDataReal(sendBuffer, 0, len + 2)) {
                                break;
                            }

                            if (callbackTimeout != null) {
                                if (callbackTimeout.socketAdapter == null) {
                                    break;
                                }

                                callbackTimeout.timeout = UtilContext.getCurrentTime() + timeout;
                            }
                        }

                    } catch (Throwable e) {
                        Environment.throwable(e);

                    } finally {
                        activePool.remove(streamIndex);
                        UtilPipedStream.closeCloseable(inputStream);
                        UtilPipedStream.closeCloseable(pipeOutput);

                        _debugInfo("SocketAdapterSel sendData InputStream close at " + streamIndex);
                        sendData(sendDataBytes(0, SocketAdapter.getVarintsLengthBytes(streamIndex), true, false, STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
                    }
                }
            };

            if (registered && sendData(buffer)) {
                sended = 1;
                UtilContext.getThreadPoolExecutor().execute(postRunnable);
                sended = 2;
                return null;
            }

            if (callbackTimeout != null) {
                //有callbackTimeout 可以延迟发送流
                RegisteredRunnable runnable = new RegisteredRunnable() {

                    int sended = 0;

                    @Override
                    public void timeout() {
                        super.timeout();
                        try {
                            activePool.remove(streamIndex);
                            UtilPipedStream.closeCloseable(inputStream);
                            UtilPipedStream.closeCloseable(pipeOutput);

                            if (sended == 1) {
                                // 通知流关闭
                                sendData(sendDataBytes(0, SocketAdapter.getVarintsLengthBytes(streamIndex), true, false, STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
                            }

                        } catch (Throwable e) {
                            Environment.throwable(e);
                        }
                    }

                    @Override
                    protected void doRun() {
                        try {
                            failed = !sendData(buffer);
                            if (!failed) {
                                sended = 1;
                                UtilContext.getThreadPoolExecutor().execute(postRunnable);
                                sended = 2;
                            }

                        } finally {
                            if (sended < 2) {
                                activePool.remove(streamIndex);
                                UtilPipedStream.closeCloseable(inputStream);
                                UtilPipedStream.closeCloseable(pipeOutput);

                                if (sended == 1) {
                                    // 通知流关闭
                                    sendData(sendDataBytes(0, SocketAdapter.getVarintsLengthBytes(streamIndex), true, false, STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
                                }
                            }
                        }
                    }
                };

                sended = 2;
                return runnable;
            }

            return null;

        } finally {
            if (sended < 2) {
                activePool.remove(streamIndex);
                UtilPipedStream.closeCloseable(inputStream);
                UtilPipedStream.closeCloseable(pipeOutput);

                if (sended == 1) {
                    // 通知流关闭
                    sendData(sendDataBytes(0, SocketAdapter.getVarintsLengthBytes(streamIndex), true, false, STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
                }
            }
        }
    }

    /**
     * 发送目标数据
     */
    @Override
    public void sendStreamIndex(int callbackIndex, byte[] dataBytes, boolean head, boolean human,
                                InputStream inputStream, Closeable pipeOutput, int timeout, CallbackAdapter callbackAdapter, Runnable inputRunnable) {
        if (inputStream == null) {
            sendDataIndex(callbackIndex, dataBytes, head, human, null, timeout, callbackAdapter);

        } else {
            CallbackTimeout callbackTimeout = null;
            if (callbackAdapter != null) {
                callbackTimeout = putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
            }

            RegisteredRunnable registeredRunnable = sendStream(dataBytes, human, callbackIndex, inputStream, pipeOutput, callbackTimeout, timeout, inputRunnable);
            if (callbackTimeout != null) {
                callbackTimeout.setRegisteredRunnable(registeredRunnable);
            }
        }
    }

    public static interface CallbackAdapterStream extends CallbackAdapter {

        public void doWith(SocketAdapter adapter, int offset, byte[] buffer, InputStream inputStream);
    }

}
