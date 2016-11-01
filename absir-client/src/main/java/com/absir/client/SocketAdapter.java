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
import com.absir.core.kernel.KernelString;
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

    public static final byte ERROR_OR_SPECIAL_FLAG = 0x01 << 3;

    public static final byte POST_FLAG = 0x01 << 4;

    public static final byte CALLBACK_FLAG = 0x01 << 5;

    public static final byte HUMAN_FLAG = 0x01 << 6;

    public static final byte URI_DICT_FLAG = (byte) (0x01 << 7);

    public static final int VARINTS_1_LENGTH = 0x7F;

    public static final int VARINTS_2_LENGTH = VARINTS_1_LENGTH + (0x7F << 7);

    public static final int VARINTS_3_LENGTH = VARINTS_2_LENGTH + (0x7F << 14);

    public static final int VARINTS_4_LENGTH = VARINTS_3_LENGTH + (0x7F << 22);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocketAdapter.class);
    protected static Map<Integer, String> varints_Uri;
    protected static Map<String, Integer> uri_Varints;
    protected static int varints_uri_Index;
    private static TimeoutThread timeoutThread;
    protected boolean registered;
    protected boolean receiveStarted;
    protected int lengthIndex;
    protected int buffLength;
    protected byte[] buff;
    protected int buffLengthIndex;
    protected Socket receiveSocket;
    protected int maxDisconnectCount = 2;

    protected long varintsServerTime;

    protected Map<String, Integer> uriVarints;

    private LinkedList<RegisteredRunnable> registeredRunnables = new LinkedList<RegisteredRunnable>();

    private CallbackAdapter receiveCallback;

    private Map<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>> receiveCallbacks = new HashMap<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>>();

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

    public static void printException(Throwable e) {
        printException(e);
    }

    public static int addVarintsUri(String uri) {
        if (varints_Uri == null) {
            synchronized (SocketAdapter.class) {
                if (varints_Uri == null) {
                    varints_Uri = new HashMap<Integer, String>();
                    uri_Varints = new HashMap<String, Integer>();
                }
            }
        }

        Integer index = uri_Varints.get(uri);
        if (index != null) {
            return index;
        }

        synchronized (varints_Uri) {
            index = uri_Varints.get(uri);
            if (index != null) {
                return index;
            }

            varints_Uri.put(++varints_uri_Index, uri);
            uri_Varints.put(uri, varints_uri_Index);
            return varints_uri_Index;
        }
    }

    public static String getVarintsUri(Integer index) {
        return varints_Uri == null ? null : varints_Uri.get(index);
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

    /**
     * 添加超时回调
     */
    public static void addCallbackTimeout(CallbackTimeout callbackTimeout) {
        startTimeout().add(callbackTimeout);
    }

    public static int getVarints(byte[] buffer, int offset, int length) {
        length -= offset;
        if (length <= 0) {
            return 0;
        }

        int b = buffer[offset];
        int varints = b & 0x7F;
        if (length > 1 && (b & 0x80) != 0) {
            b = buffer[++offset];
            varints += (b & 0x7F) << 7;

            if (length > 2 && (b & 0x80) != 0) {
                b = buffer[++offset];
                varints += (b & 0x7F) << 14;

                if (length > 3 && (b & 0x80) != 0) {
                    b = buffer[++offset];
                    varints += (b & 0x7F) << 22;
                }
            }
        }

        return varints;
    }

    public static int getVarintsLength(byte[] buffer, int offset, int length) {
        length -= offset;
        if (length <= 0) {
            return 0;
        }

        int len = 1;
        int b = buffer[offset];
        if (length > 1 && (b & 0x80) != 0) {
            len++;
            b = buffer[++offset];
            if (length > 2 && (b & 0x80) != 0) {
                len++;
                b = buffer[++offset];
                if (length > 3 && (b & 0x80) != 0) {
                    len++;
                }
            }
        }

        return len;
    }

    public static int getVarintsLength(int varints) {
        if (varints <= VARINTS_1_LENGTH) {
            return 1;
        }

        if (varints <= VARINTS_2_LENGTH) {
            return 2;
        }

        if (varints <= VARINTS_3_LENGTH) {
            return 3;
        }

        return 4;
    }

    public static final byte[] getVarintsLengthBytes(int varints) {
        byte[] bytes = new byte[getVarintsLength(varints)];
        setVarintsLength(bytes, 0, varints);
        return bytes;
    }

    public static final void setVarintsLength(byte[] destination, int destionationIndex, int length) {
        if (length > VARINTS_1_LENGTH) {
            destination[destionationIndex] = (byte) ((length & 0x7F) | 0x80);
            if (length > VARINTS_2_LENGTH) {
                destination[++destionationIndex] = (byte) (((length >> 7) & 0x7F) | 0x80);
                if (length > VARINTS_3_LENGTH) {
                    destination[++destionationIndex] = (byte) (((length >> 14) & 0x7F) | 0x80);
                    destination[++destionationIndex] = (byte) ((length >> 22) & 0x7F);

                } else {
                    destination[++destionationIndex] = (byte) ((length >> 14) & 0x7F);
                }

            } else {
                destination[++destionationIndex] = (byte) ((length >> 7) & 0x7F);
            }

        } else {
            destination[destionationIndex] = (byte) (length & 0x7F);
        }
    }

    public void clearUriVarints() {
        if (uriVarints != null) {
            uriVarints.clear();
        }
    }

    public void addUriVarints(String uri, Integer varints) {
        if (uriVarints == null) {
            synchronized (this) {
                if (uriVarints == null) {
                    uriVarints = new HashMap<String, Integer>();
                }
            }
        }

        uriVarints.put(uri, varints);
    }

    public Integer getUriVarints(String uri) {
        return uriVarints == null ? null : uriVarints.get(uri);
    }

    public boolean isConnecting() {
        return receiveStarted || tryConnecting;
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
        setRegistered(registered, 0);
    }

    public void setRegistered(boolean registered, long serverTime) {
        if (this.registered != registered) {
            this.registered = registered;
            if (serverTime == 0 || varintsServerTime != serverTime) {
                varintsServerTime = serverTime;
                clearUriVarints();
            }

        } else if (varintsServerTime != serverTime) {
            varintsServerTime = serverTime;
            clearUriVarints();
        }
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
//        if (callbackAdapter == null) {
//            return null;
//        }

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
        return 1024;
    }

    public int getMaxCallbackIndex() {
        return VARINTS_4_LENGTH;
    }

    public int getMaxBufferLength() {
        return 204800;
    }

    public synchronized int generateCallbackIndex() {
        int minCallbackIndex = getMinCallbackIndex();
        int maxCallbackIndex = getMaxCallbackIndex();
        while (true) {
            if (++callbackIndex < minCallbackIndex || callbackIndex >= maxCallbackIndex) {
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

    public final void addDisconnectNumber() {
        if (++disconnectNumber >= Integer.MAX_VALUE) {
            disconnectNumber = 1;
        }
    }

    public void close() {
        addDisconnectNumber();
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

        } else {
            addDisconnectNumber();
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
            if (registerCallback != null) {
                registerCallback.doWith(this, 0, buffer);
            }

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
            if ((flag & CALLBACK_FLAG) != 0) {
                //压缩模式
                int varints = getVarints(buffer, offset, length);
                offset += getVarintsLength(varints);
                callbackIndex = varints;
            }

            if (offset < length && (flag & URI_DICT_FLAG) != 0) {
                //返回数据中包含url压缩字典
                int varints1 = getVarints(buffer, offset, length);
                offset += getVarintsLength(varints1);

                if (offset < length) {
                    int varints2 = getVarints(buffer, offset, length);
                    offset += getVarintsLength(varints2);

                    if (varints2 > 0) {
                        String uri = getVarintsUri(varints1);
                        if (!KernelString.isEmpty(uri)) {
                            addUriVarints(uri, varints2);
                        }
                    }
                }

                if (offset >= length) {
                    return;
                }
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
     * @param human
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(byte[] dataBytes, boolean head, boolean human, int callbackIndex, byte[] postData) {
        return sendDataBytes(0, dataBytes, head, human, callbackIndex, postData);
    }

    /**
     * 生成发送数据包
     *
     * @param off
     * @param dataBytes
     * @param head
     * @param human
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean human, int callbackIndex,
                                byte[] postData) {
        return sendDataBytes(off, dataBytes, head, human, 0, callbackIndex, postData);
    }

    /**
     * 生成发送数据包
     *
     * @param off
     * @param dataBytes
     * @param head
     * @param human
     * @param flag
     * @param callbackIndex
     * @param postData
     * @return
     */
    public byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean human, int flag, int callbackIndex,
                                byte[] postData) {
        return sendDataBytes(off, dataBytes, 0, dataBytes == null ? 0 : dataBytes.length, head, human, (byte) flag,
                callbackIndex, postData, 0, postData == null ? 0 : postData.length);
    }

    public byte[] sendDataBytes(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean human,
                                byte flag, int callbackIndex, byte[] postData, int postOff, int postLen) {
        return sendDataBytes(off, dataBytes, dataOff, dataLen, head, human, flag, callbackIndex, postData, postOff, postLen, false);
    }

    public byte[] sendDataBytes(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean human,
                                byte flag, int callbackIndex, byte[] postData, int postOff, int postLen, boolean noPLen) {
        dataLen -= dataOff;

        int cLen = callbackIndex == 0 ? 0 : getVarintsLength(callbackIndex);

        postLen = postData == null ? 0 : (postLen - postOff);
        int pLen = noPLen || postLen <= 0 ? 0 : getVarintsLength(postLen);

        byte headFlag = flag;
        if (headFlag != 0) {
            head = true;
        }

        int length = off + dataLen;
        if (cLen > 0) {
            head = true;
            headFlag |= CALLBACK_FLAG;
            length += cLen;
        }

        if (pLen > 0) {
            head = true;
            headFlag |= POST_FLAG;
            length += pLen;
        }

        if (postLen > 0) {
            length += postLen;
        }

        if (head) {
            length++;
        }

        int offLen = getVarintsLength(length) + off;
        byte[] sendDataBytes = new byte[offLen + length];
        setVarintsLength(sendDataBytes, 0, length);

        if (head) {
            if (human) {
                headFlag |= HUMAN_FLAG;
            }

            sendDataBytes[offLen++ - off] = headFlag;
            if (cLen > 0) {
                setVarintsLength(sendDataBytes, offLen, callbackIndex);
                offLen += cLen;
            }

            if (pLen > 0) {
                setVarintsLength(sendDataBytes, offLen, postLen);
                offLen += pLen;
            }
        }

        System.arraycopy(dataBytes, dataOff, sendDataBytes, offLen, dataLen);
        offLen += dataLen;
        if (postLen > 0 && postData != null) {
            System.arraycopy(postData, postOff, sendDataBytes, offLen, postLen);
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

    protected RegisteredRunnable registerSendData(final byte[] buffer) {
        connect();
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
     * @param human
     * @param postData
     * @param timeout
     * @param callbackAdapter
     */
    public void sendData(byte[] dataBytes, boolean head, boolean human, byte[] postData, int timeout,
                         CallbackAdapter callbackAdapter) {
        sendDataIndex(generateCallbackIndex(), dataBytes, head, human, postData, timeout, callbackAdapter);
    }

    /**
     * 发送目标数据
     *
     * @param callbackIndex
     * @param dataBytes
     * @param head
     * @param human
     * @param postData
     * @param timeout
     * @param callbackAdapter
     */
    public void sendDataIndex(int callbackIndex, byte[] dataBytes, boolean head, boolean human, byte[] postData,
                              int timeout, CallbackAdapter callbackAdapter) {
        sendDataCallback(callbackIndex, sendDataBytes(dataBytes, head, human, callbackIndex, postData), timeout, callbackAdapter);
    }

    public void sendDataCallback(int callbackIndex, byte[] data, int timeout, CallbackAdapter callbackAdapter) {
        CallbackTimeout callbackTimeout = null;
        if (callbackAdapter != null) {
            callbackTimeout = putReceiveCallbacks(callbackIndex, timeout, callbackAdapter);
        }

        RegisteredRunnable registeredRunnable = registerSendData(data);
        if (callbackTimeout != null) {
            callbackTimeout.registeredRunnable = registeredRunnable;
        }

        if (registeredRunnable != null) {
            connect();
        }
    }

    public byte[] sendDataBytesVarints(String uri, int callback, byte[] postBytes, int postOff, int postLen) {
        return sendDataBytesVarints(0, uri, false, callback, postBytes, postOff, postLen);
    }

    public byte[] sendDataBytesVarints(int off, String uri, boolean human, int callback, byte[] postBytes, int postOff, int postLen) {
        byte flag = URI_DICT_FLAG;
        byte[] dataBytes;
        Integer index = getUriVarints(uri);
        if (index == null) {
            //没找到压缩字典，添加压缩回调参数
            flag |= ERROR_OR_SPECIAL_FLAG;
            int uriVarints = addVarintsUri(uri);
            int uriLength = getVarintsLength(uriVarints);
            dataBytes = uri.getBytes();
            byte[] bytes = sendDataBytes(off + uriLength, dataBytes, 0, dataBytes.length, true, human, flag, callback, postBytes, postOff, postLen, true);
            setVarintsLength(bytes, getVarintsLength(bytes, 0, bytes.length) + 1 + off, uriVarints);
            return bytes;

        } else {
            //找到压缩字典
            dataBytes = getVarintsLengthBytes(index);
            return sendDataBytes(off, dataBytes, 0, dataBytes.length, true, human, flag, callback, postBytes, postOff, postLen, false);
        }
    }

    // 支持字典压缩
    public void sendDataIndexVarints(String uri, byte[] postBytes, int timeout, CallbackAdapter callbackAdapter) {
        sendDataIndexVarints(generateCallbackIndex(), uri, postBytes, timeout, callbackAdapter);
    }

    public void sendDataIndexVarints(int callbackIndex, String uri, byte[] postBytes, int timeout, CallbackAdapter callbackAdapter) {
        sendDataCallback(callbackIndex, sendDataBytesVarints(0, uri, false, callbackIndex, postBytes, 0, postBytes.length), timeout, callbackAdapter);
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

        private final List<CallbackTimeout> addTimeouts = new ArrayList<CallbackTimeout>();
        /**
         * 超时执行队列
         */
        private final List<CallbackTimeout> callbackTimeouts = new LinkedList<CallbackTimeout>();
        private boolean stopped;

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
