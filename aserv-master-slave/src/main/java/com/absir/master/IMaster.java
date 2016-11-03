package com.absir.master;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.client.value.Rpc;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/10/27.
 */
@Rpc
public interface IMaster {

    @JaLang("时间")
    public long time() throws IOException;

    public InputStream test(InputStream inputStream);

    public InputStream test1(String name, InputStream inputStream);

    public void test2(String name, InputStream inputStream);

}
