package com.absir.slave;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.value.Rpc;
import com.absir.server.value.Body;

import java.io.IOException;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface ISlave {

    @JaLang("时间")
    public long time();

    @JaLang("同步服务实体")
    public void merge(String entityName, @Body byte[] postData) throws IOException;

    @JaLang("同步服务实体")
    public void option(String entityName, int option, @Body byte[] postData);

}
