package com.absir.client.rpc;

/**
 * Created by absir on 16/9/1.
 */
public interface IRpcAdapter {

    /*
            SocketAdapter.CallbackAdapter example
            byte[] postBytes = length == 0 ? null : HelperDataFormat.PACK.writeAsBytesArray(args);
            final UtilAtom atom = new UtilAtom();
            atom.increment();
            rpcAdapter.sendDataIndexVarints(rpcMethod.attribute, rpcMethod.uri, postBytes, new SocketAdapter.CallbackAdapter() {
                @Override
                public void doWith(SocketAdapter adapter, int offset, byte[] buffer) {
                    try {
                        if (adapter == null) {
                            rpcReturn.code = 1;
                        }

                        int length = buffer == null ? 0 : buffer.length;
                        int code = rpcReturn.code = SocketAdapter.getVarints(buffer, offset, length);
                        if (code == 1) {
                            rpcReturn.value = HelperDataFormat.PACK.read(buffer, offset + SocketAdapter.getVarintsLength(code), length, rpcMethod.returnType);
                        }

                    } catch (Throwable e) {
                        LOGGER.error("rpc invoker error method = " + method, e);

                    } finally {
                        atom.decrement();
                    }
                }
            });

            atom.await();
     */

    public byte[] paramData(RpcInterface.RpcAttribute attribute, Object[] args);

    public Object sendDataIndexVarints(RpcInterface.RpcAttribute attribute, String uri, byte[] paramData);

}
