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

/**
 * @author absir
 */
public class InputMaster extends InputSocketImpl {

    /**
     * @param model
     * @param inputSocketAtt
     * @param socketChannel
     */
    public InputMaster(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
        super(model, inputSocketAtt, socketChannel);
    }

    /**
     * @param input
     * @return
     */
    public static boolean onAuthentication(Input input) {
        if (input instanceof InputMaster) {
            return true;
        }

        String slvky = input.getParam("slvky");
        if (slvky != null) {
            String[] slvkys = slvky.split("_");
            if (slvkys.length == 2) {
                String id = slvkys[0];
                ChannelContext channelContext = InputMasterContext.ME.getServerContext().getChannelContexts().get(id);
                if (channelContext != null && KernelObject.equals(InputMasterContext.ME.getSlaveKey(id), slvky)) {
                    if (input.getAddress()
                            .equals(channelContext.getChannel().socket().getInetAddress().getHostAddress())) {
                        return true;

                    } else {
                        String slvhash = input.getParam("slvhash");
                        if (slvhash != null && slvhash.equals(
                                HelperEncrypt.encryptionMD5(slvky, InputMasterContext.ME.getKey().getBytes()))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
