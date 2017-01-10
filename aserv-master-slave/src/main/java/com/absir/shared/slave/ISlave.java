package com.absir.shared.slave;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.rpc.RpcData;
import com.absir.client.value.Rpc;
import com.absir.shared.bean.SlaveUpgrade;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface ISlave {

    @JaLang("时间")
    public long time();

    // slaveUpgrade == null is stop
    @JaLang("升级")
    public RpcData upgrade(SlaveUpgrade slaveUpgrade);


}
