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

    public String getEid(RpcInterface.RpcAttribute attribute) {
        int index = attribute.rpcData;
        if (index <= 0) {
            return uri;
        }

        int length = args == null ? null : args.length;
        if (length <= 0) {
            return uri;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (index > length) {
            index = length;
        }

        stringBuilder.append(uri);
        for (int i = 0; i < index; i++) {
            stringBuilder.append('@');
            stringBuilder.append(args[i].toString());
        }

        return stringBuilder.toString();
    }
}
