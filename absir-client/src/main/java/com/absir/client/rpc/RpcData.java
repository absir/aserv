package com.absir.client.rpc;

/**
 * Created by absir on 16/9/9.
 */
public class RpcData {

    protected Object[] args;

    protected String uri;

    protected byte[] paramData;

    public Object[] getArgs() {
        return args;
    }

    public String getUri() {
        return uri;
    }

    public byte[] getParamData() {
        return paramData;
    }
}
