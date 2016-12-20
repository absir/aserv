package com.absir.thrift;

import com.absir.server.in.InModel;
import com.absir.server.socket.InputSocketImpl;

import java.nio.channels.SocketChannel;

/**
 * Created by absir on 2016/12/20.
 */
public class ThriftInput extends InputSocketImpl {

    public ThriftInput(InModel model, InputSocketAtt inputSocketAtt, SocketChannel socketChannel) {
        super(model, inputSocketAtt, socketChannel);
    }
}
