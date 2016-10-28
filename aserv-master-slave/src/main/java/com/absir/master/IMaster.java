package com.absir.master;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.value.Rpc;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface IMaster {

    @JaLang("时间")
    public long time();

}
