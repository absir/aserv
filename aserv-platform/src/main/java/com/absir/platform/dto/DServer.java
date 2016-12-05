package com.absir.platform.dto;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.platform.bean.JServer;

/**
 * Created by absir on 2016/12/5.
 */
public class DServer extends JServer.ServerEntry {

    @JaLang(value = "节点服务", tag = "slaveServer")
    public long id;

    @JaLang("名称")
    public String name;

    @JaLang("服务地址")
    public String serverAddress;

    @JaLang("端口")
    public int port;

    @JaLang("下载地址")
    public String downloadAddress;

}
