/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年4月14日 上午12:42:28
 */
package com.absir.master;

import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelObject;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.socket.InputSocketImpl;
import com.absir.server.socket.SocketServerContext.ChannelContext;

import java.nio.channels.SocketChannel;

public class InputMaster extends InputSocketImpl {

    public InputMaster(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
        super(model, inputSocketAtt, socketChannel);
    }

    public static boolean onAuthentication(Input input) {
        if (input instanceof InputMaster) {
            return true;
        }

        String slaveKey = input.getParam("_sly");
        if (slaveKey != null) {
            String[] slaveKeys = slaveKey.split("_");
            if (slaveKeys.length == 2) {
                String id = slaveKeys[0];
                ChannelContext channelContext = InputMasterContext.ME.getServerContext().getChannelContexts().get(id);
                if (channelContext != null && KernelObject.equals(InputMasterContext.ME.getSlaveKey(id), slaveKey)) {
                    if (input.getAddress()
                            .equals(channelContext.getChannel().socket().getInetAddress().getHostAddress())) {
                        return true;

                    } else {
                        String slaveHash = input.getParam("_slh");
                        if (slaveHash != null && slaveHash.equals(
                                HelperEncrypt.encryptionMD5(slaveKey, InputMasterContext.ME.getKey().getBytes()))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
