package com.absir.master;

import com.absir.client.rpc.RpcSocketAdapter;

/**
 * Created by absir on 2016/11/14.
 */
public class MasterRpcAdapter extends RpcSocketAdapter<MasterChannelAdapter> {

    public MasterRpcAdapter(MasterChannelAdapter adapter) {
        super(adapter);
    }

}
