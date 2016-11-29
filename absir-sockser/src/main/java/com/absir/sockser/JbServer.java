/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-18 下午2:31:55
 */
package com.absir.sockser;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiActive;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbServer extends JbBean implements JiActive, JiServer {

    @JaLang(value = "服务名称", tag = "serverName")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String name;

    @JaLang("端口号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int port;

    @JaLang("混合端口")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean multiPort;

    @JaLang("IP")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String ip;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long beginTime;

    @JaLang("关闭时间")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long passTime;

    @JaLang("关闭")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean closed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isMultiPort() {
        return multiPort;
    }

    public void setMultiPort(boolean multiPort) {
        this.multiPort = multiPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
