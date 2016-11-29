package com.absir.shared.slave;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.rpc.RpcData;
import com.absir.client.value.Rpc;
import com.absir.shared.bean.SlaveStatus;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface ISlave {

    @JaLang("时间")
    public long time();

    @JaLang("准备升级")
    public RpcData readyUpgrade(SlaveStatus slaveStatus);

    @JaLang("执行升级")
    public void doUpgrade(SlaveStatus slaveStatus);

}
