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

/**
 * @author absir
 *
 */
@JaModel(desc = true)
@MappedSuperclass
public abstract class JbSession extends JbVerifier {

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户编号")
    private Long userId;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户名")
    private String username;

    @JaEdit(types = "ip", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "IP地址", tag = "ip")
    private long address;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "设备", tag = "device")
    private String agent;

    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最后登录", tag = "lastLogin")
    private long lastTime;

    @JaLang("附加信息")
    @Lob
    private byte[] metas;

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the address
     */
    public long getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(long address) {
        this.address = address;
    }

    /**
     * @return the agent
     */
    public String getAgent() {
        return agent;
    }

    /**
     * @param agent
     *            the agent to set
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * @return the lastTime
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * @param lastTime
     *            the lastTime to set
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * @return the metas
     */
    public byte[] getMetas() {
        return metas;
    }

    /**
     * @param metas
     *            the metas to set
     */
    public void setMetas(byte[] metas) {
        this.metas = metas;
    }
}
