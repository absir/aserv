/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月7日 上午11:20:38
 */
package com.absir.client;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelByte;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.Socket;
import java.util.*;

public class SocketAdapter {

    public static final byte[] bit = "b".getBytes();

    public static final byte[] ok = "ok".getBytes();

    public static final byte[] failed = "failed".getBytes();

    public static final byte STREAM_FLAG = 0x01;

    public static final byte STREAM_CLOSE_FLAG = 0x01 << 1;

    public static final byte RESPONSE_FLAG = 0x01 << 2;

    public static final byte ERROR_FLAG = 0x01 << 3;

    public static final byte POST_FLAG = 0x01 << 4;

    public static final byte CALLBACK_FLAG = 0x01 << 5;

    public static final byte DEBUG_FLAG = 0x01 << 6;

    public static final byte VARINTS_FLAG = (byte) (0x01 << 7);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketAdapter.class);

    private static TimeoutThread timeoutThread;

    protected boolean registered;

    private LinkedList<RegisteredRunnable> registeredRunnables = new LinkedList<RegisteredRunnable>();

    private CallbackAdapter receiveCallback;

    private Map<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>> receiveCallbacks = new HashMap<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>>();

    protected boolean receiveStarted;

    protected int lengthIndex;

    protected int buffLength;

    protected byte[] buff;

    protected int buffLengthIndex;

    protected Socket receiveSocket;

    private int retryConnect;

    private Socket socket;

    private byte[] beats = bit;

    private long beatLifeTime;

    private CallbackAdapter callbackConnect;

    private CallbackAdapter callbackDisconnect;

    private CallbackAdapter acceptCallback;

    private CallbackAdapter registerCallback;

    private int callbackIndex;

    private Socket acceptSocket;

    private boolean tryConnecting;

    private int disconnectNumber;

    protected int maxDisconnectCount = 1;

    protected boolean varints = true;

    public boolean isVarints() {
        return varints;
    }

    public void setVarints(boolean varints) {
        this.varints = varints;
    }

    public static void printException(Throwable e) {
        printException(e);
    }

    /**
     * 开启超时线程
     */
    public static TimeoutThread startTimeout() {
        TimeoutThread thread = timeoutThread;
        if (thread == null) {
            synchronized (SocketAdapter.class) {
                if (timeoutThread == null) {
                    timeoutThread = new TimeoutThread();
                    timeoutThread.setName("SocketAdapter.TimeoutThread");
                    timeoutThread.setDaemon(true);
                    timeoutThread.start();
                }

                thread = timeoutThread;
            }
        }

        return thread;
    }

    /**
     * 关闭超时线程
     */
    public static void stopTimeout() {
        if (timeoutThread != null) {
            synchronized (SocketAdapter.class) {
                if (timeoutThread != null) {
                    timeoutThread.interrupt();
                    timeoutThread = null;
                }
            }
        }
    }

    public boolean isConnecting() {
        return receiveStarted || tryConnecting;
    }

    /**
     * 添加超时回调
     *
     * @param callbackTimeout
     */
    public static void addCallbackTimeout(CallbackTimeout callbackTimeout) {
        startTimeout().add(callbackTimeout);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public byte[] getBeats() {
        return beats;
    }

    public void setBeats(byte[] beats) {
        this.beats = beats;
    }

    public long getBeatLifeTime() {
        return beatLifeTime;
    }

    public void setBeatLifeTime(long beatLifeTime) {
        this.beatLifeTime = beatLifeTime;
    }

    public CallbackAdapter getCallbackConnect() {
        return callbackConnect;
    }

    public void setCallbackConnect(CallbackAdapter callbackConnect) {
        this.callbackConnect = callbackConnect;
    }

    public CallbackAdapter getCallbackDisconnect() {
        return callbackDisconnect;
    }

    public void setCallbackDisconnect(CallbackAdapter callbackDisconnect) {
        this.callbackDisconnect = callbackDisconnect;
    }

    public CallbackAdapter getAcceptCallback() {
        return acceptCallback;
    }

    public void setAcceptCallback(CallbackAdapter acceptCallback) {
        this.acceptCallback = acceptCallback;
    }

    public CallbackAdapter getRegisterCallback() {
        return registerCallback;
    }

    public void setRegisterCallback(CallbackAdapter registerCallback) {
        this.registerCallback = registerCallback;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public LinkedList<RegisteredRunnable> getRegisteredRunnables() {
        return registeredRunnables;
    }

    public int getCallbackIndex() {
        return callbackIndex;
    }

    public CallbackAdapter getReceiveCallback() {
        return receiveCallback;
    }

    public void setReceiveCallback(CallbackAdapter receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public Map<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>> getReceiveCallbacks() {
        return receiveCallbacks;
    }

    public CallbackTimeout putReceiveCallbacks(int callbackIndex, int timeout, CallbackAdapter callbackAdapter) {
        ObjectEntry<CallbackAdapter, CallbackTimeout> entry = new ObjectEntry<CallbackAdapter, CallbackTimeout>(
                callbackAdapter, null);
        CallbackTimeout callbackTimeout = null;
        if (timeout > 0 && callbackIndex > getMinCallbackIndex()) {
            callbackTimeout = new CallbackTimeout();
            callbackTimeout.timeout = System.currentTimeMillis() + timeout;
            callbackTimeout.socketAdapter = this;
            callbackTimeout.callbackIndex = callbackIndex;
            addCallbackTimeout(callbackTimeout);
            entry.setValue(callbackTimeout);
        }

        receiveCallbacks.put(callbackIndex, entry);
        return callbackTimeout;
    }

    public int getMinCallbackIndex() {
        return 2048;
    }

    public int getMaxBufferLength() {
        return 204800;
    }

    public synchronized int generateCallbackIndex() {
        int minCallbackIndex = getMinCallbackIndex();
        while (true) {
            if (++callbackIndex < minCallbackIndex || callbackIndex >= Integer.MAX_VALUE) {
                callbackIndex = minCallbackIndex + 1;
            }

            if (!receiveCallbacks.containsKey(callbackIndex)) {
                break;
            }
        }

        return callbackIndex;
    }

    public void clearRetryConnect() {
        retryConnect = 0;
    }

    public boolean isRetryConnectMax() {
        return retryConnect >= 3;
    }

    /**
     * 开始连接
     */
    public void connect() {
        if (socket != null && (beatLifeTime <= System.currentTimeMillis() || !socket.isConnected())) {
            disconnect(socket);
        }

        if (tryConnecting) {
            return;
        }

        if (socket == null && callbackConnect != null) {
            synchronized (this) {
                try {
                    tryConnecting = true;
                    if (socket == null && !isRetryConnectMax()) {
                        retryConnect++;
                        callbackConnect.doWith(this, 0, null);
                        if (socket != null) {
                            waiteAccept();
                        }
                    }

                } finally {
                    tryConnecting = false;
                }
            }
        }
    }

    public final int getDisconnectNumber() {
        return disconnectNumber;
    }

    public final int getDisconnectCount(int number) {
        if (disconnectNumber < number) {
            return Integer.MAX_VALUE - number + disconnectNumber;

        } else {
            return disconnectNumber - number;
        }
    }

    public final int getMaxDisconnectCount() {
        return maxDisconnectCount;
    }

    public void setMaxDisconnectCount(int maxDisconnectCount) {
        this.maxDisconnectCount = maxDisconnectCount;
    }

    public void close() {
        if (++disconnectNumber >= Integer.MAX_VALUE) {
            disconnectNumber = 1;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (Exception e) {
            }
        }

        socket = null;
        registered = false;
        receiveStarted = false;
    }

    /**
     * 断开连接
     *
     * @param st
     */
    public void disconnect(Socket st) {
        if (st == null || st == socket) {
            synchronized (this) {
                if (st == null || st == socket) {
                    close();
                    if (Environment.isActive() && callbackDisconnect != null) {
                        callbackDisconnect.doWith(this, 0, null);
                    }
                }
            }
        }
    }

    /**
     * 最新心跳
     */
    public void lastedBeat() {
        beatLifeTime = System.currentTimeMillis() + 60000;
    }

    /**
     * 接受心跳
     */
    public void receiverBeat() {
        lastedBeat();
    }

    /**
     * 等待连接
     */
    public void waiteAccept() {
        lastedBeat();
    }

    /**
     * 最新登录
     */
    public boolean lastedRegister() {
        if (registered) {
            lastedBeat();
            afterRegisterRunnable();
            return true;
        }

        return false;
    }

    public void afterRegisterRunnable() {
        clearRetryConnect();
        if (registered) {
            // 执行登录等待请求
            if (!registeredRunnables.isEmpty()) {
                synchronized (this) {
                    try {
                        List<RegisteredRunnable> runnables = registeredRunnables;
                        registeredRunnables = new LinkedList<RegisteredRunnable>();
                        boolean failed = false;
                        Socket st = socket;
                        for (RegisteredRunnable runnable : runnables) {
                            if (failed) {
                                if (runnable.removed) {

                                } else {
                                    registeredRunnables.add(runnable);
                                }

                            } else {
                                runnable.run();
                                failed = runnable.failed;
                                if (failed) {
                                    registeredRunnables.add(runnable);
                                    disconnect(st);
                                }
                            }
                        }

                    } catch (Throwable e) {
                        printException(e);
                    }
                }
            }
        }
    }

    protected void addRegisterRunnable(RegisteredRunnable registeredRunnable) {
        synchronized (this) {
            registeredRunnables.add(registeredRunnable);
        }

        afterRegisterRunnable();
    }

    /**
     * 接收数据
     *
     * @param st
     * @param buffer
     * @param off
     * @param len
     */
    public void receiveByteBuffer(Socket st, byte[] buffer, int off, int len) {
        if (st != socket) {
            return;
        }

        for (; off < len; off++) {
            if (buff == null) {
                if (lengthIndex < 4) {
                    if (varints) {
                        byte b = buffer[off];
                        switch (lengthIndex) {
                            case 0:
                                buffLength = b & 0x7F;
                                break;
                            case 1:
                                buffLength = (b & 0x7F) << 7;
                                break;
                            case 2:
                                buffLength = (b & 0x7F) << 14;
                                break;
                            case 3:
                                buffLength = (b & 0x7F) << 22;
                                break;
                        }

                        if (lengthIndex < 3 && (b & 0x80) == 0) {
                            lengthIndex = 3;
                        }

                    } else {
                        int length = buffer[off] & 0xFF;
                        if (lengthIndex > 0) {
                            length = buffLength + (length << (8 * lengthIndex));
                        }

                        buffLength = length;
                    }

                    if (++lengthIndex == 4) {
                        if (buffLength >= 0 && buffLength < getMaxBufferLength()) {
                            buff = new byte[buffLength];
                            buffLengthIndex = 0;
                            if (buffLength == 0) {
                                receiveBuffDone();
                            }

                        } else {
                            lengthIndex = 0;
                            buffLength = 0;
                        }
                    }
                }

            } else {
                buff[buffLengthIndex] = buffer[off];
                if (++buffLengthIndex >= buffLength) {
                    receiveBuffDone();
                    continue;
                }
            }
        }
    }

    /**
     * 清除数据
     */
    protected final void clearReceiveBuff() {
        lengthIndex = 0;
        buffLength = 0;
        buff = null;
    }

    /**
     * 接收完成
     */
    public void receiveBuffDone() {
        byte[] buffer = buff;
        clearReceiveBuff();
        receiveBuffDone(buffer);
    }

    /**
     * 接收完成数据
     *
     * @param buffer
     */
    public void receiveBuffDone(byte[] buffer) {
        int length = buffer.length;
        // 检测心跳
        if (beats != null && beats.length == buffer.length) {
            int i;
            for (i = 0; i < length; i++) {
                if (buffer[i] != beats[i]) {
                    break;
                }
            }

            if (i >= length) {
                receiverBeat();
                return;
            }
        }

        // 接收请求
        if (acceptSocket != socket) {
            acceptSocket = socket;
            if (acceptCallback != null) {
                acceptCallback.doWith(this, 0, buffer);
                lastedRegister();
                return;
            }
        }

        // 注册请求
        if (!registered) {
            registerCallback.doWith(this, 0, buffer);
            lastedRegister();
            return;
        }

        int offset;
        byte flag;
        if (length == 0) {
            offset = 0;
            flag = 0;

        } else {
            offset = 1;
            flag = buffer[0];
        }

        receiveCallback(offset, buffer, flag);
    }

    public void receiveCallback(int offset, byte[] buffer, byte flag) {
        // 转发请求
        int length = buffer.length;
        Integer callbackIndex = null;
        if (length > 1) {
            if ((flag & VARINTS_FLAG) != 0) {
                int b = buffer[1];
                int index = b & 0x7F;
                if (length > 2 && (b & 0x80) != 0) {
                    b = buffer[2];
                    index += (b & 0x7F) << 7;
                    if (length > 3 && (b & 0x80) != 0) {
                        b = buffer[3];
                        index += (b & 0x7F) << 14;
                        if (length > 4 && (b & 0x80) != 0) {
                            b = buffer[4];
                            index += (b & 0x7F) << 22;
                        }
                    }
                }

                callbackIndex = index;

            } else if (length > 4 && (flag & CALLBACK_FLAG) != 0) {
                offset += 4;
                int index = buffer[1] & 0xFF;
                index += (buffer[2] & 0xFF) << 8;
                index += (buffer[3] & 0xFF) << 16;
                index += (buffer[4] & 0xFF) << 24;
                callbackIndex = index;
            }
        }

        receiveCallback(offset, buffer, flag, callbackIndex);
    }

    /**
     * 接收数据回调
     *
     * @param offset
     * @param buffer
     * @param flag
     * @param callbackIndex
     */
    public void receiveCallback(int offset, byte[] buffer, byte flag, Integer callbackIndex) {
        if (callbackIndex != null) {
            boolean minCallback = callbackIndex <= getMinCallbackIndex();
            ObjectEntry<CallbackAdapter, CallbackTimeout> entry = minCallback ? receiveCallbacks.get(callbackIndex)
                    : receiveCallbacks.remove(callbackIndex);
            if (entry != null) {
                if (minCallback) {
                    entry.getKey().doWith(this, offset, buffer);

                } else {
                    synchronized (entry) {
                        CallbackAdapter callbackAdapter = entry.getKey();
                        if (callbackAdapter != null) {
                            entry.setKey(null);
                            CallbackTimeout callbackTimeout = entry.getValue();
                            if (callbackTimeout != null) {
                                callbackTimeout.socketAdapter = null;
                            }

                            callbackAdapter.doWith(this, offset, buffer);
                        }
                    }
                }

                return;
            }
        }

        if (buffer != null && receiveCallback != null) {
            receiveCallback(receiveCallback, offset, buffer, flag, callbackIndex);
        }
    }

    public void receiveCallback(CallbackAdapter callbackAdapter, int offset, byte[] buffer, byte flag,
                                Integer callbackIndex) {
        callbackAdapter.doWith(this, offset, buffer);
    }

    /**
     * 生成发送数据包
     *
     * @param dataBytes
     * @param head
     * @param debug
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(byte[] dataBytes, boolean head, boolean debug, int callbackIndex, byte[] postData) {
        return sendDataBytes(0, dataBytes, head, debug, callbackIndex, postData);
    }

    /**
     * 生成发送数据包
     *
     * @param off
     * @param dataBytes
     * @param head
     * @param debug
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean debug, int callbackIndex,
                                byte[] postData) {
        return sendDataBytes(off, dataBytes, head, debug, 0, callbackIndex, postData);
    }

    /**
     * 生成发送数据包
     *
     * @param off
     * @param dataBytes
     * @param head
     * @param debug
     * @param flag
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean debug, int flag, int callbackIndex,
                                byte[] postData) {
        return sendDataBytes(off, dataBytes, 0, dataBytes == null ? 0 : dataBytes.length, head, debug, flag,
                callbackIndex, postData, 0, postData == null ? 0 : postData.length);
    }

    public static final int VARINTS_1_LENGTH = 0x7F;

    public static final int VARINTS_2_LENGTH = VARINTS_1_LENGTH + (0x7F << 7);

    public static final int VARINTS_3_LENGTH = VARINTS_2_LENGTH + (0x7F << 14);

    public static final int VARINTS_4_LENGTH = VARINTS_3_LENGTH + (0x7F << 22);

    public byte[] sendDataBytes(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean debug,
                                int flag, int callbackIndex, byte[] postData, int postOff, int postLen) {
        byte headFlag = 0x00;
        int headLength = off + (callbackIndex == 0 ? 0 : 4);
        if (head) {
            headLength++;
        } else if (callbackIndex != 0) {
            head = true;
            headLength++;
        }

        int dataLength = dataLen - dataOff;
        int length;
        byte[] sendDataBytes;
        if (postData == null) {
            // no post
            length = dataLength += headLength;
            if (varints) {
                dataLength += KernelByte.getVarintsLength(dataLength);

            } else {
                dataLength += 4;
            }

            sendDataBytes = new byte[dataLength];
            if (dataBytes != null) {
                System.arraycopy(dataBytes, dataOff, sendDataBytes, headLength, dataLength - headLength);
            }

        } else {
            // post head
            if (!head) {
                head = true;
                headLength++;
            }

            headFlag |= POST_FLAG;
            headLength += 4;
            int postLength = postLen - postOff;
            length = dataLength += headLength + postLength;
            if (varints) {
                dataLength += KernelByte.getVarintsLength(dataLength);

            } else {
                dataLength += 4;
            }

            sendDataBytes = new byte[dataLength];
            if (dataBytes != null) {
                System.arraycopy(dataBytes, dataOff, sendDataBytes, headLength, dataLength - headLength - postLength);
            }

            System.arraycopy(postData, postOff, sendDataBytes, dataLength - postLength, postLength);
            sendDataBytes[headLength - 4] = (byte) postLength;
            sendDataBytes[headLength - 3] = (byte) (postLength >> 8);
            sendDataBytes[headLength - 2] = (byte) (postLength >> 16);
            sendDataBytes[headLength - 1] = (byte) (postLength >> 24);
        }

        // headFlag
        if (head) {
            if (debug) {
                headFlag |= DEBUG_FLAG;
            }

            if (callbackIndex != 0) {
                headFlag |= CALLBACK_FLAG;
                sendDataBytes[off + 5] = (byte) callbackIndex;
                sendDataBytes[off + 6] = (byte) (callbackIndex >> 8);
                sendDataBytes[off + 7] = (byte) (callbackIndex >> 16);
                sendDataBytes[off + 8] = (byte) (callbackIndex >> 24);
            }

            headFlag |= flag;
            sendDataBytes[off + 4] = headFlag;
        }

        // send data bytes length
        if (varints) {
            if (length > VARINTS_1_LENGTH) {
                sendDataBytes[0] = (byte) ((length & 0x7F) | 0x80);
                if (length > VARINTS_2_LENGTH) {
                    sendDataBytes[1] = (byte) (((length >> 7) & 0x7F) | 0x80);
                    if (length > VARINTS_3_LENGTH) {
                        sendDataBytes[2] = (byte) (((length >> 14) & 0x7F) | 0x80);
                        sendDataBytes[3] = (byte) ((length >> 22) & 0x7F);

                    } else {
                        sendDataBytes[2] = (byte) ((length >> 14) & 0x7F);
                    }

                } else {
                    sendDataBytes[1] = (byte) ((length >> 7) & 0x7F);
                }

            } else {
                sendDataBytes[0] = (byte) (length & 0x7F);
            }

        } else {
            sendDataBytes[0] = (byte) length;
            sendDataBytes[1] = (byte) (length >> 8);
            sendDataBytes[2] = (byte) (length >> 16);
            sendDataBytes[3] = (byte) (length >> 24);
        }

        return sendDataBytes;
    }

    public boolean sendData(byte[] buffer) {
        return sendData(buffer, 0, buffer.length);
    }

    public boolean sendData(byte[] buffer, int offset, int length) {
        Socket sendSocket = socket;
        if (sendSocket != null) {
            try {
                sendSocket.getOutputStream().write(buffer);
                return true;

            } catch (Exception e) {
                printException(e);
            }

            disconnect(sendSocket);
        }

        return false;
    }

    protected RegisteredRunnable sendData(byte[] dataBytes, boolean head, boolean debug, int callbackIndex,
                                          byte[] postData) {
        connect();
        final byte[] buffer = sendDataBytes(dataBytes, head, debug, callbackIndex, postData);
        if (registered && sendData(buffer)) {
            return null;
        }

        RegisteredRunnable runnable = new RegisteredRunnable() {

            @Override
            public void doRun() {
                failed = !sendData(buffer);
            }
        };
        addRegisterRunnable(runnable);
        afterRegisterRunnable();
        return runnable;
    }

    protected void receiveSocketChannel() {
        receiveSocket = socket;
        clearReceiveBuff();
        try {
            int buffLength = 0;
            byte[] buffer = new byte[256];
            InputStream inputStream = receiveSocket.getInputStream();
            while (Environment.isActive() && receiveSocket == socket && (buffLength = inputStream.read(buffer)) > 0) {
                receiveByteBuffer(receiveSocket, buffer, 0, buffLength);
            }

        } catch (Exception e) {
            printException(e);
        }

        disconnect(receiveSocket);
    }

    /**
     * 接收数据线程开启
     */
    public synchronized void receiveSocketChannelStart() {
        if (receiveStarted) {
            return;
        }

        receiveStarted = true;
        Thread thread = new Thread() {

            @Override
            public void run() {
                receiveSocketChannel();
            }
        };

        // 设置为守护线程
        thread.setName("SocketAdapter.receiveSocketChannelStart");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 发送回调方法
     *
     * @param dataBytes
     * @param head
     * @param debug
     * @param postData
     * @param timeout
     * @param callbackAdapter
     */
    public void sendData(byte[] dataBytes, boolean head, boolean debug, byte[] postData, int timeout,
                         CallbackAdapter callbackAdapter) {
        sendDataIndex(generateCallbackIndex(), dataBytes, head, debug, postData, timeout, callbackAdapter);
    }

    /**
     * 发送目标数据
     *
     * @param callbackIndex
     * @param dataBytes
     * @param head
     * @param debug
     * @param postData
     * @param timeout
     * @param callbackAdapter
     */
    public void sendDataIndex(int callbackIndex, byte[] dataBytes, boolean head, boolean debug, byte[] postData,
                              int timeout, CallbackAdapter callbackAdapter) {
        CallbackTimeout callbackTimeout = null;
        if (callbackAdapter != null) {
            callbackTimeout = putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
        }

        RegisteredRunnable registeredRunnable = sendData(dataBytes, head, debug, callbackIndex, postData);
        if (callbackTimeout != null) {
            callbackTimeout.registeredRunnable = registeredRunnable;
        }

        if (registeredRunnable != null) {
            connect();
        }
    }

    public static interface CallbackAdapter {

        public void doWith(SocketAdapter adapter, int offset, byte[] buffer);
    }

    public static abstract class RegisteredRunnable {

        protected boolean failed;

        protected boolean removed;

        public void run() {
            if (!removed) {
                doRun();
            }
        }

        /**
         * 执行
         */
        protected abstract void doRun();
    }


    public static class CallbackTimeout {

        public int disconnectNumber;

        public long timeout;

        public RegisteredRunnable registeredRunnable;

        public SocketAdapter socketAdapter;

        public int callbackIndex;

        public boolean isAdapterDisconnect() {
            SocketAdapter adapter = socketAdapter;
            if (adapter == null) {
                return true;

            } else {
                if (disconnectNumber == 0) {
                    disconnectNumber = adapter.getDisconnectNumber();

                } else {
                    if (!adapter.isConnecting() || adapter.getDisconnectCount(disconnectNumber) > adapter.getMaxDisconnectCount()) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * 超时执行
         */
        public void run() {
            RegisteredRunnable runnable = registeredRunnable;
            if (runnable != null) {
                runnable.removed = true;
            }

            SocketAdapter adapter = socketAdapter;
            if (adapter != null) {
                try {
                    adapter.receiveCallback(0, null, (byte) 0, callbackIndex);

                } catch (Throwable e) {
                    LOGGER.error("socket adapter timeout error", e);
                }

                socketAdapter = null;
            }
        }
    }

    protected static class TimeoutThread extends Thread {

        private boolean stopped;

        private final List<CallbackTimeout> addTimeouts = new ArrayList<CallbackTimeout>();

        /**
         * 超时执行队列
         */
        private final List<CallbackTimeout> callbackTimeouts = new LinkedList<CallbackTimeout>();

        public final boolean isStopped() {
            return stopped;
        }

        public synchronized void add(CallbackTimeout timeout) {
            if (stopped) {
                if (timeout.socketAdapter != null) {
                    timeout.run();
                }

            } else {
                addTimeouts.add(timeout);
            }
        }

        private void clearTimeout(boolean stop) {
            for (CallbackTimeout callbackTimeout : callbackTimeouts) {
                callbackTimeout.run();
            }

            synchronized (this) {
                if (stop) {
                    stopped = true;
                }

                for (CallbackTimeout callbackTimeout : addTimeouts) {
                    callbackTimeout.run();
                }

                callbackTimeouts.clear();
                addTimeouts.clear();
            }
        }

        @Override
        public void run() {
            try {
                // 超时执行检测
                long contextTime;
                long lastTime = 0;
                Iterator<CallbackTimeout> iterator;
                CallbackTimeout callbackTimeout;
                while (Environment.isActive()) {
                    Thread.sleep(2000);
                    contextTime = System.currentTimeMillis();
                    // 防止系统时间往后设置, 超时失败
                    if (contextTime < lastTime) {
                        clearTimeout(true);
                    }

                    if (!addTimeouts.isEmpty()) {
                        synchronized (this) {
                            callbackTimeouts.addAll(addTimeouts);
                            addTimeouts.clear();
                        }
                    }

                    iterator = callbackTimeouts.iterator();
                    while (iterator.hasNext()) {
                        callbackTimeout = iterator.next();
                        if (callbackTimeout.timeout <= contextTime || callbackTimeout.isAdapterDisconnect()) {
                            callbackTimeout.run();
                            iterator.remove();
                        }
                    }
                }

            } catch (InterruptedException e) {
            }

            clearTimeout(true);
        }
    }
}
