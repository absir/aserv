/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月5日 下午7:58:19
 */
package com.absir.master.bean.dto;

import com.absir.aserv.system.bean.value.JaLang;

public class DServer {

    @JaLang(value = "服务编号", tag = "serverId")
    public long id;

    @JaLang("名称")
    public String name;

    @JaLang("开启时间")
    public int openTime;

    @JaLang("IP")
    public String ip;

    @JaLang("端口")
    public int port;

    @JaLang("状态")
    // 0 代开 1 开启 2 维护
    public int status;

    @JaLang("路径")
    public String path;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOpenTime() {
        return openTime;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

}
