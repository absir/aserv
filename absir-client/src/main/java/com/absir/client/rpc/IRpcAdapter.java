package com.absir.client.rpc;

import java.io.IOException;

/**
 * Created by absir on 16/9/1.
 */
public interface IRpcAdapter {

    public byte[] paramData(RpcInterface.RpcAttribute attribute, Object[] args) throws IOException;

    public Object sendDataIndexVarints(RpcInterface.RpcAttribute attribute, String uri, byte[] paramData, Object[] args, Class<?>[] parameterTypes, Class<?> returnType) throws IOException;

}
