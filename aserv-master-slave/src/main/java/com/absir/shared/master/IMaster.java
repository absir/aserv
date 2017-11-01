package com.absir.shared.master;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.value.Rpc;
import com.absir.shared.bean.EUpgradeStatus;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface IMaster {

    @JaLang("时间")
    public long time() throws IOException;

    @JaLang("下载")
    public InputStream download(String filePath) throws IOException;

    @JaLang("升级状态")
    @Rpc(rpcData = 0)
    public void upgradeStatues(EUpgradeStatus status, String param, boolean failed);

}
