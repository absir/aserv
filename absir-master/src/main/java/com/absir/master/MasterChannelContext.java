package com.absir.master;

import com.absir.server.socket.SocketServerContext;

import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 2016/11/10.
 */
public class MasterChannelContext extends SocketServerContext.ChannelContext {

    protected String id;

    protected String slaveKey;

    protected MasterRpcAdapter masterRpcAdapter;

    public MasterChannelContext(String id, SocketChannel channel) {
        super(channel);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getSlaveKey() {
        return slaveKey;
    }

    public MasterRpcAdapter getMasterRpcAdapter() {
        if (masterRpcAdapter == null) {
            masterRpcAdapter = initMasterRpcAdapter(id);
        }

        masterRpcAdapter.getSocketAdapter().setChannel(channel);
        return masterRpcAdapter;
    }

    public MasterChannelAdapter getMasterChannelAdapter() {
        return getMasterRpcAdapter().getSocketAdapter();
    }

    private static Map<Serializable, MasterRpcAdapter> masterChannelMapAdapter;

    protected static MasterRpcAdapter initMasterRpcAdapter(String id) {
        if (masterChannelMapAdapter == null) {
            synchronized (MasterChannelContext.class) {
                if (masterChannelMapAdapter == null) {
                    masterChannelMapAdapter = new HashMap<Serializable, MasterRpcAdapter>();
                }
            }
        }

        MasterRpcAdapter adapter = masterChannelMapAdapter.get(id);
        if (adapter == null) {
            synchronized (masterChannelMapAdapter) {
                adapter = masterChannelMapAdapter.get(id);
                if (adapter == null) {
                    adapter = InputMasterContext.ME.createMasterRpcAdapter(id);
                    masterChannelMapAdapter.put(id, adapter);
                }
            }
        }

        return adapter;
    }
}
