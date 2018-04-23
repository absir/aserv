package com.absir.thrift;

import com.absir.client.SocketAdapter;
import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2017/3/21.
 */
public class TSocketAdapter implements SocketAdapter.CallbackAdapter {

    protected static String encryptKey = "absir.thrift";

    protected Object holdEncryptKey;

    private SocketAdapter socketAdapter;

    public TSocketAdapter(SocketAdapter socketAdapter) {
        this.socketAdapter = socketAdapter;
        socketAdapter.setRegisterCallback(this);
    }

    public SocketAdapter getSocketAdapter() {
        return socketAdapter;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public static void setEncryptKey(String encryptKey) {
        TSocketAdapter.encryptKey = encryptKey;
    }

    public void registerEncryptKey(String encryptKey) {
        if (KernelString.isEmpty(encryptKey)) {
            holdEncryptKey = null;

        } else {
            holdEncryptKey = HelperEncrypt.getSROREncryptKey(getEncryptKey() + encryptKey);
        }
    }

    @Override
    public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
        byte[] ok = SocketAdapter.ok;
        int length = ok.length;
        if (buffer.length >= length) {
            for (int i = 0; i < length; i++) {
                if (buffer[i] != ok[i]) {
                    registerFail(socketAdapter);
                    return;
                }
            }

            long serverTime = 0;
            if (buffer.length > ++length) {
                String[] params = StringUtils.split(new String(buffer, length, buffer.length - length), ",", 3);
                serverTime = KernelDyna.to(params[0], long.class);
                registerEncryptKey(params.length == 1 ? null : params[1]);
            }

            adapter.setRegistered(true, serverTime);
        }
    }

    protected void registerFail(SocketAdapter socketAdapter) {
        socketAdapter.disconnect(null);
    }

    public byte[] encrypt(byte[] buffer) throws IOException {
        if (holdEncryptKey == null) {
            return buffer;

        } else {
            return HelperEncrypt.encryptSRORKey(buffer, (byte[]) holdEncryptKey);
        }
    }

    public InputStream decrypt(int offset, byte[] buffer) throws IOException {
        if (holdEncryptKey != null) {
            HelperEncrypt.decryptSRORKey(buffer, offset, buffer.length, (byte[]) holdEncryptKey);
        }

        return new ByteArrayInputStream(buffer, offset, buffer.length);
    }

}
