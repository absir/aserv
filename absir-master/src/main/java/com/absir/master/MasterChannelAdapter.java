package com.absir.master;

import com.absir.client.SocketAdapter;
import com.absir.client.SocketAdapterSel;
import com.absir.client.SocketNIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by absir on 2016/11/10.
 */
public class MasterChannelAdapter extends SocketAdapterSel {

    protected SocketChannel channel;

    @Override
    public void connect() {
    }

    @Override
    public synchronized void receiveSocketChannelStart() {
    }

    @Override
    public void close() {
        super.close();
        channel = null;
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

            return true;

        } catch (IOException e) {
            close();
        }

        return false;
    }

    @Override
    public byte[] sendDataBytesReal(int off, byte[] dataBytes, int dataOff, int dataLen, boolean head, boolean human, byte flag, int callbackIndex, byte[] postData, int postOff, int postLen, boolean noPLen) {
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
    public byte[] sendDataBytesVarintsReal(int off, String uri, boolean human, byte flag, int callbackIndex, byte[] postBytes, int postOff, int postLen) {
        flag |= URI_DICT_FLAG;
        byte[] dataBytes;
        Integer index = getUriVarints(uri);
        if (index == null) {
            //没找到压缩字典，添加压缩回调参数
            flag |= ERROR_OR_SPECIAL_FLAG;
            int uriVarints = addVarintsUri(uri);
            int uriLength = getVarintsLength(uriVarints);
            dataBytes = uri.getBytes();
            byte[] bytes = sendDataBytesReal(off + uriLength, dataBytes, 0, dataBytes.length, true, human, flag, callbackIndex, postBytes, postOff, postLen, false);
            setVarintsLength(bytes, getVarintsLength(bytes, 0, bytes.length) + 2 + off, uriVarints);
            return bytes;

        } else {
            //找到压缩字典
            dataBytes = getVarintsLengthBytes(index);
            return sendDataBytesReal(off, dataBytes, 0, dataBytes.length, true, human, flag, callbackIndex, postBytes, postOff, postLen, true);
        }
    }
}
