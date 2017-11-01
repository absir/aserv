package com.absir.master;

import com.absir.client.rpc.RpcData;
import com.absir.client.rpc.RpcInterface;
import com.absir.client.rpc.RpcSocketAdapter;

/**
 * Created by absir on 2016/11/14.
 */
public class MasterRpcAdapter extends RpcSocketAdapter<MasterChannelAdapter> {

    public MasterRpcAdapter(MasterChannelAdapter adapter) {
        super(adapter);
    }

    @Override
    protected void resolverRpcData(RpcInterface.RpcAttribute attribute, RpcData rpcData) {
        super.resolverRpcData(attribute, rpcData);
        InputMasterContext.ME.resolverRpcData(attribute, rpcData, getSocketAdapter());
    }

}
