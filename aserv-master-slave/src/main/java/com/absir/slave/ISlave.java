package com.absir.slave;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.value.Rpc;
import com.absir.server.value.Body;

import java.io.IOException;
import java.io.InputStream;

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

    public InputStream test(InputStream inputStream);

    public InputStream test1(String name, InputStream inputStream);

    public void test2(String name, InputStream inputStream);


    public String[] param(String[] name);

    public String[] paramRS(String[] name) throws IOException;

    @Rpc(sendStream = true)
    public String[] paramSS(String[] name);

    @Rpc(sendStream = true)
    public String[] paramSRS(String[] name) throws IOException;

}
