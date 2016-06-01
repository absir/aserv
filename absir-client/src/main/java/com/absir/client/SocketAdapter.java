/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月7日 上午11:20:38
 */
package com.absir.client;

import com.absir.core.base.Environment;
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

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketAdapter.class);

    private static TimeoutThread timeoutThread;

    protected boolean registered;

    protected LinkedList<RegisteredRunnable> registeredRunnables = new LinkedList<RegisteredRunnable>();

    protected CallbackAdapter receiveCallback;

    protected Map<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>> receiveCallbacks = new HashMap<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>>();

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

    public static void printException(Throwable e) {
        printException(e);
    }

    /**
     * 开启超时线程
     */
    public static TimeoutThread startTimeout() {
        TimeoutThread thead = timeoutThread;
        if (thead == null) {
            synchronized (SocketAdapter.class) {
                if (timeoutThread == null) {
                    timeoutThread = new TimeoutThread();
                    timeoutThread.setName("SocketAdapter.TimeoutThread");
                    timeoutThread.setDaemon(true);
                    timeoutThread.start();
                }

                thead = timeoutThread;
            }
        }

        return thead;
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

    private boolean tryConntecting;

    /**
     * 开始连接
     */
    public void connect() {
        if (socket != null && (beatLifeTime <= System.currentTimeMillis() || !socket.isConnected())) {
            disconnect(socket);
        }

        if (tryConntecting) {
            return;
        }

        if (socket == null && callbackConnect != null) {
            synchronized (this) {
                try {
                    tryConntecting = true;
                    if (socket == null && !isRetryConnectMax()) {
                        retryConnect++;
                        callbackConnect.doWith(this, 0, null);
                        if (socket != null) {
                            waiteAccept();
                        }
                    }

                } finally {
                    tryConntecting = false;
                }
            }
        }
    }

    public void close() {
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
    public boolean lastedResgiter() {
        if (registered) {
            clearRetryConnect();
            lastedBeat();
            // 执行登录等待请求
            if (!registeredRunnables.isEmpty()) {
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

            return true;
        }

        return false;
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
                    int length = buffer[off] & 0xFF;
                    if (lengthIndex > 0) {
                        length = buffLength + (length << (8 * lengthIndex));
                    }

                    buffLength = length;
                    lengthIndex++;
                    if (lengthIndex == 4) {
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
                lastedResgiter();
                return;
            }
        }

        // 注册请求
        if (!registered) {
            registerCallback.doWith(this, 0, buffer);
            lastedResgiter();
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
        if (length > 4 && (flag & CALLBACK_FLAG) != 0) {
            offset += 4;
            int index = buffer[1] & 0xFF;
            index += (buffer[2] & 0xFF) << 8;
            index += (buffer[3] & 0xFF) << 16;
            index += (buffer[4] & 0xFF) << 24;
            callbackIndex = index;
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

    public byte[] sendDataBytes(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean debug,
                                int flag, int callbackIndex, byte[] postData, int postOff, int postLen) {
        byte headFlag = 0x00;
        int headLength = off + (callbackIndex == 0 ? 4 : 8);
        if (head) {
            headLength++;
        } else if (callbackIndex != 0) {
            head = true;
            headLength++;
        }

        int dataLength = dataLen - dataOff;
        byte[] sendDataBytes;
        if (postData == null) {
            // no post
            dataLength += headLength;
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
            dataLength += headLength + postLength;
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
        dataLength -= 4;
        sendDataBytes[0] = (byte) dataLength;
        sendDataBytes[1] = (byte) (dataLength >> 8);
        sendDataBytes[2] = (byte) (dataLength >> 16);
        sendDataBytes[3] = (byte) (dataLength >> 24);
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

        registeredRunnables.add(runnable);
        //lastedResgiter();
        clearRetryConnect();
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

        public long timeout;

        public RegisteredRunnable registeredRunnable;

        public SocketAdapter socketAdapter;

        public int callbackIndex;

        /**
         * 超时执行
         */
        public void run() {
            if (registeredRunnable != null) {
                registeredRunnable.removed = true;
            }

            if (socketAdapter != null) {
                try {
                    socketAdapter.receiveCallback(0, null, (byte) 0, callbackIndex);

                } catch (Throwable e) {
                    LOGGER.error("socket adapter timeout run", e);
                }
            }
        }
    }

    protected static class TimeoutThread extends Thread {

        private final List<CallbackTimeout> addTimeouts = new ArrayList<CallbackTimeout>();

        /**
         * 超时执行队列
         */
        private final List<CallbackTimeout> callbackTimeouts = new LinkedList<CallbackTimeout>();

        public synchronized void add(CallbackTimeout timeout) {
            addTimeouts.add(timeout);
        }

        @Override
        public void run() {
            try {
                // 超时执行检测
                long contextTime;
                Iterator<CallbackTimeout> iterator;
                CallbackTimeout callbackTimeout;
                while (Environment.isActive()) {
                    Thread.sleep(5000);
                    contextTime = System.currentTimeMillis();
                    if (!addTimeouts.isEmpty()) {
                        synchronized (this) {
                            callbackTimeouts.addAll(addTimeouts);
                            addTimeouts.clear();
                        }
                    }

                    iterator = callbackTimeouts.iterator();
                    while (iterator.hasNext()) {
                        callbackTimeout = iterator.next();
                        if (callbackTimeout.socketAdapter == null || callbackTimeout.timeout <= contextTime) {
                            callbackTimeout.run();
                            iterator.remove();
                        }
                    }
                }

            } catch (InterruptedException e) {
            }
        }
    }
}
