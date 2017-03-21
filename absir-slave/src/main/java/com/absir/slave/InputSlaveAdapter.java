/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月9日 下午9:17:18
 */
package com.absir.slave;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.callback.CallbackMsg;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.data.helper.HelperDataFormat;
import com.absir.slave.resolver.ISlaveCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class InputSlaveAdapter extends SocketAdapterSel {

    protected String ip;

    protected int port;

    protected String group;

    protected String key;

    protected String url;

    protected String slaveKey;

    /**
     * 初始化
     */
    public InputSlaveAdapter(String ip, int port, String group, String key, String url,
                             ISlaveCallback[] slaveCallbacks) {
        this.ip = ip;
        this.port = port;
        this.group = group;
        this.key = key;
        this.url = url;
        setCallbackConnect(new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                connectAdapter(adapter);
            }
        });

        setCallbackDisconnect(new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                disconnectAdapter(adapter);
            }
        });

        setAcceptCallback(new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                acceptAdapter(adapter, buffer);
            }
        });

        setRegisterCallback(new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                registerAdapter(adapter, buffer);
            }
        });

        setReceiveCallback(new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                receiveCallback(adapter, buffer);
            }
        });

        if (slaveCallbacks != null) {
            Map<Integer, ObjectEntry<CallbackAdapter, CallbackTimeout>> receiveCallbacks = getReceiveCallbacks();
            for (ISlaveCallback slaveCallback : slaveCallbacks) {
                int callbackIndex = slaveCallback.getCallbackIndex();
                if (callbackIndex <= getMinCallbackIndex() && !receiveCallbacks.containsKey(callbackIndex)) {
                    putReceiveCallbacks(callbackIndex, 0, slaveCallback);
                }
            }
        }

        slaveKey = key;
        // 接受密钥
        putReceiveCallbacks(2, 0, new CallbackAdapter() {

            @Override
            public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                slaveKey = new String(buffer, offset, buffer.length - offset);
            }
        });
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getGroup() {
        return group;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    /**
     * 连接
     */
    protected void connectAdapter(SocketAdapter adapter) {
        if (ip != null) {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(ip, port));
                adapter.setSocket(socketChannel.socket());
                //adapter.lastedBeat();
                adapter.receiveSocketChannelStart();

            } catch (Exception e) {
                LOGGER.error("connectAdapter error", e);
            }
        }
    }

    /**
     * 断开重连
     */
    protected void disconnectAdapter(SocketAdapter adapter) {
        adapter.connect();
    }

    /**
     * 接收
     */
    protected void acceptAdapter(SocketAdapter adapter, byte[] buffer) {
        adapter.sendData(InputSlaveContext.ME.registerData(this, buffer));
    }

    /**
     * 注册
     */
    protected void registerAdapter(SocketAdapter adapter, byte[] buffer) {
        long serverTime = InputSlaveContext.ME.getRegisterServerTime(this, buffer);
        if (serverTime >= 0) {
            adapter.setRegistered(true, serverTime);

        } else {
            LOGGER.error("registerAdapter failed status : " + new String(buffer));
            adapter.close();
        }
    }

    /**
     * 接收数据
     */
    protected void receiveCallback(SocketAdapter adapter, byte[] buffer) {
        // LOGGER.info("receiveCallback" + buffer);
    }

    public void sendData(String uri, Object postData, CallbackMsg<?> callbackMsg) throws IOException {
        sendData(uri, postData, 30000, callbackMsg);
    }

    public void sendData(String uri, Object postData, int timeout, CallbackMsg<?> callbackMsg) throws IOException {
        sendData(uri.getBytes(ContextUtils.getCharset()), true, false,
                postData == null ? null : HelperDataFormat.PACK.writeAsBytes(postData), timeout, callbackMsg);
    }

}
