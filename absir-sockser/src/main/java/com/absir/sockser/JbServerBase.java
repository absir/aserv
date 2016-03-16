package com.absir.sockser;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiActive;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月11日 下午3:48:52
 */

@MappedSuperclass
public class JbServerBase extends JbBase implements JiActive, JiServer {

    @Id
    @JaLang("纪录编号")
    private Long id;

    @JaLang("服务器名称")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String name;

    @JaLang("端口号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int port;

    @JaLang("混合端口")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean multiPort;

    @JaLang("服务器IP")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
