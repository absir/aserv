/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月27日 下午7:20:15
 */
package com.absir.client;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.util.UtilActivePool;
import com.absir.core.util.UtilAtom;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.core.util.UtilPipedStream.NextOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketAdapterSel extends SocketAdapter {

    public static final long PIPED_STREAM_TIMEOUT = 30000;

    public static final int POST_BUFF_LEN = 1024;

    private static int buffSize = 1024;

    private static Selector selector;

    private static UtilAtom atom;

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
                        selector = Selector.open();
                        atom = new UtilAtom();
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
                                                    socketAdapter.receiveByteBuffer(socketChannel.socket(), array, 0,
                                                            length);
                                                    continue;
                                                }

                                            } catch (Exception e) {
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

                                    } catch (Exception e) {
                                        Environment.throwable(e);
                                        break;
                                    }
                                }
                            }

                            ;
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
        if (pipedStream != null) {
            pipedStream.close();
            pipedStream = null;
        }

        if (activePool != null) {
            activePool.clear();
        }
    }

    @Override
    public boolean sendData(byte[] buffer, int offset, int length) {
        Socket socket = getSocket();
        if (socket != null) {
            SocketChannel socketChannel = getSocket().getChannel();
            if (socketChannel != null && !socketChannel.isBlocking()) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, offset, length);
                synchronized (socketChannel) {
                    try {
                        SocketNIO.writeTimeout(socketChannel, byteBuffer);
                        return true;

                    } catch (Exception e) {
                        Environment.throwable(e);
                    }
                }

                disconnect(socket);
                return false;
            }
        }

        return super.sendData(buffer, offset, length);
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
                int offLen = offset + streamIndexLen;
                // 写入流信息
                NextOutputStream outputStream = getPipedStream().getOutputStream(streamIndex);
                if (outputStream != null) {
                    try {
                        outputStream.write(buffer, offLen, length);
                        return;

                    } catch (IOException e) {
                        Environment.throwable(e);
                    }
                }

                sendData(sendDataBytes(0, buffer, offset, offset + streamIndexLen, true, false, STREAM_CLOSE_FLAG, 0, null, 0, 0, true));
                return;
            }

        } else if ((flag & STREAM_CLOSE_FLAG) != 0) {
            int length = buffer.length;
            int streamIndex = getVarints(buffer, offset, length);
            if (length == offset + getVarintsLength(streamIndex)) {
                if ((flag & POST_FLAG) == 0) {
                    //接收关闭
                    NextOutputStream outputStream = getPipedStream().getOutputStream(streamIndex);
                    if (outputStream != null) {
                        try {
                            outputStream.close();

                        } catch (IOException e) {
                            Environment.throwable(e);
                        }
                    }

                } else {
                    //发送关闭
                    if (activePool != null) {
                        getActivePool().remove(streamIndex);
                    }
                }

                return;
            }
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
            try {
                NextOutputStream outputStream = callbackAdapter instanceof CallbackAdapterStream ? createNextOutputStream(streamIndex) : null;
                if (outputStream == null) {
                    // 不是CallbackAdapterStream 不能接受流数据返回
                    sendData(sendDataBytes(0, buffer, offset, offset + streamIndexLen, true, false, STREAM_CLOSE_FLAG, 0, null, 0, 0, true));

                } else {
                    // 生成PipedInputStream执行回调
                    PipedInputStream inputStream = new PipedInputStream();
                    inputStream.connect(outputStream);
                    CallbackAdapterStream callbackAdapterStream = (CallbackAdapterStream) callbackAdapter;
                    callbackAdapter = null;
                    callbackAdapterStream.doWith(this, offset + streamIndexLen, buffer, inputStream);
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

            } catch (IOException e) {
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
                                            final InputStream inputStream, final CallbackTimeout callbackTimeout, final long timeout) {
        connect();
        int sended = 0;
        final ObjectTemplate<Integer> template = getActivePool().addObject();
        if (template == null) {
            return null;
        }

        final int streamIndex = template.object;
        final int streamIndexLen = getVarintsLength(streamIndex);
        try {
            final byte[] buffer = sendDataBytes(streamIndexLen, dataBytes, true, human, (byte) (STREAM_FLAG | POST_FLAG), callbackIndex, null);
            int offLen = getVarintsLength(buffer, 0, 4) + 1;
            setVarintsLength(buffer, offLen, streamIndex);
            final Runnable postRunnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        byte[] sendBuffer = sendDataBytes(streamIndexLen, null, 0, 0, true, false, STREAM_FLAG, 0,
                                null, 0, getPostBuffLen(), true);
                        setVarintsLength(sendBuffer, 3, streamIndex);
                        int postOff = 3 + streamIndexLen;
                        int length = sendBuffer.length - postOff;
                        int len;
                        try {
                            while ((len = inputStream.read(sendBuffer, postOff, length)) > 0) {
                                len += postOff - 2;
                                sendBuffer[0] = (byte) ((len & 0x7F) | 0x80);
                                sendBuffer[1] = (byte) ((len >> 7) & 0x7F);

                                if (template.object == null || !sendData(sendBuffer, 0, len + 2)) {
                                    return;
                                }

                                if (callbackTimeout != null) {
                                    if (callbackTimeout.socketAdapter == null) {
                                        return;
                                    }

                                    callbackTimeout.timeout = UtilContext.getCurrentTime() + timeout;
                                }
                            }

                        } catch (Exception e) {
                            Environment.throwable(e);
                            return;
                        }

                    } finally {
                        activePool.remove(streamIndex);
                        UtilPipedStream.closeCloseable(inputStream);
                    }

                    sendData(sendDataBytes(0, KernelByte.getLengthBytes(streamIndex), true, false, STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
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
                        activePool.remove(streamIndex);
                        UtilPipedStream.closeCloseable(inputStream);
                        if (sended == 1) {
                            // 通知流关闭
                            sendData(sendDataBytes(0, KernelByte.getLengthBytes(streamIndex), true, false,
                                    STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
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
                                if (sended == 1) {
                                    // 通知流关闭
                                    sendData(sendDataBytes(0, KernelByte.getLengthBytes(streamIndex), true, false,
                                            STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
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
                if (sended == 1) {
                    // 通知流关闭
                    sendData(sendDataBytes(0, KernelByte.getLengthBytes(streamIndex), true, false,
                            STREAM_CLOSE_FLAG | POST_FLAG, 0, null));
                }
            }
        }
    }

    /**
     * 发送目标数据
     */
    @Override
    public void sendStreamIndex(int callbackIndex, byte[] dataBytes, boolean head, boolean human,
                                InputStream inputStream, int timeout, CallbackAdapter callbackAdapter) {
        if (inputStream == null) {
            sendDataIndex(callbackIndex, dataBytes, head, human, null, timeout, callbackAdapter);

        } else {
            CallbackTimeout callbackTimeout = null;
            if (callbackAdapter != null) {
                callbackTimeout = putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
            }

            RegisteredRunnable registeredRunnable = sendStream(dataBytes, human, callbackIndex, inputStream,
                    callbackTimeout, timeout);
            if (callbackTimeout != null) {
                callbackTimeout.registeredRunnable = registeredRunnable;
            }
        }
    }

    public static interface CallbackAdapterStream extends CallbackAdapter {

        public void doWith(SocketAdapter adapter, int offset, byte[] buffer, InputStream inputStream);
    }

}
