package com.absir.client.rpc;

import com.absir.client.SocketAdapter;

/**
 * Created by absir on 16/9/1.
 */
public interface IRpcAdapter {

    public long getDefaultTimeout();

    public void sendDataIndexVarints(String uri, byte[] postBytes, long timeout, SocketAdapter.CallbackAdapter callbackAdapter);
}
