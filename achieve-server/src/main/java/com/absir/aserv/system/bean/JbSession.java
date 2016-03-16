/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-11 下午1:03:49
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbVerifier;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@JaModel(desc = true)
@MappedSuperclass
public abstract class JbSession extends JbVerifier {

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户编号")
    private Long userId;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户名")
    private String username;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "地址")
    private String address;

    @JaEdit(types = "ip", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "IP")
    private long ip;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "设备", tag = "device")
    private String agent;

    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最后登录", tag = "lastLogin")
    private long lastTime;

    @JaLang("附加信息")
    @Lob
    private byte[] metas;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getIp() {
        return ip;
    }

    public void setIp(long ip) {
        this.ip = ip;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public byte[] getMetas() {
        return metas;
    }

    public void setMetas(byte[] metas) {
        this.metas = metas;
    }
}
