package com.absir.master;

import com.absir.server.socket.SocketServerContext;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * Created by absir on 2016/11/10.
 */
public class MasterChannelContext extends SocketServerContext.ChannelContext {

    protected Serializable id;

    protected String slaveKey;

    protected MasterRpcAdapter masterRpcAdapter;

    public MasterChannelContext(Serializable id, SocketChannel channel) {
        super(channel);
        this.id = id;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    public MasterRpcAdapter getMasterRpcAdapter() {
        if (masterRpcAdapter == null) {
            masterRpcAdapter = InputMasterContext.ME.getMasterRpcAdapter(id);
        }

        masterRpcAdapter.getSocketAdapter().channel = channel;
        return masterRpcAdapter;
    }

    public MasterChannelAdapter getMasterChannelAdapter() {
        return getMasterRpcAdapter().getSocketAdapter();
    }
}
