package com.absir.master;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.SocketNIO;
import com.absir.client.callback.CallbackMsg;
import com.absir.context.core.ContextUtils;
import com.absir.data.helper.HelperDataFormat;
import com.absir.server.socket.SocketServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by absir on 2016/11/10.
 */
public class MasterChannelAdapter extends SocketAdapterSel {

    private String slaveId;

    private SocketChannel channel;

    public MasterChannelAdapter(String slaveId) {
        this.slaveId = slaveId;
    }

    public String getSlaveId() {
        return slaveId;
    }

    @Override
    public Object getSendHolder() {
        return channel;
    }

    protected void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void connect() {
    }

    @Override
    public synchronized void receiveSocketChannelStart() {
    }

    @Override
    public void close() {
        super.close();
        if (channel != null) {
            try {
                SocketServer.close(channel);

            } finally {
                channel = null;
            }
        }
    }

    @Override
    public boolean sendDataReal(byte[] buffer, int offset, int length) {
        SocketChannel socketChannel = channel;
        if (socketChannel == null) {
            return false;
        }

        try {
            synchronized (socketChannel) {
                SocketNIO.writeTimeout(socketChannel, ByteBuffer.wrap(buffer, offset, length));
            }

            lastedBeat();
            return true;

        } catch (IOException e) {
            close();
        }

        return false;
    }

    @Override
    public byte[] sendDataBytesReal(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean human, byte flag, int callbackIndex, byte[] postData, int postOff, int postLen, boolean noPLen) {
        if (flag == STREAM_FLAG || (flag & STREAM_CLOSE_FLAG) != 0) {
            return super.sendDataBytesReal(off, dataBytes, dataOff, dataLen, head, human, flag, callbackIndex, postData, postOff, postLen, noPLen);
        }

        if (callbackIndex > 0) {
            flag |= RESPONSE_FLAG;

        } else {
            flag |= CALLBACK_FLAG;
        }

        byte[] bytes = super.sendDataBytesReal(off + 1, dataBytes, dataOff, dataLen, head, human, flag, callbackIndex, postData, postOff, postLen, noPLen);
        SocketAdapter.setVarintsLength(bytes, SocketAdapter.getVarintsLength(bytes, 0, bytes.length) + 1, MS_CALLBACK_INDEX);
        return bytes;
    }

    @Override
    public int getSendDataBytesHeaderLength() {
        return 1;
    }

    public void sendData(String uri, Object postData, CallbackMsg<?> callbackMsg) throws IOException {
        sendData(uri, postData, 30000, callbackMsg);
    }

    public void sendData(String uri, Object postData, int timeout, CallbackMsg<?> callbackMsg) throws IOException {
        sendData(uri.getBytes(ContextUtils.getCharset()), true, false,
                postData == null ? null : HelperDataFormat.PACK.writeAsBytes(postData), timeout, callbackMsg);
    }
}
