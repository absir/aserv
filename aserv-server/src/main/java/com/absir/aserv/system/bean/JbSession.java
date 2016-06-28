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
import org.hibernate.annotations.Type;

import javax.persistence.MappedSuperclass;
import java.util.HashMap;
import java.util.Map;

@JaModel(desc = true)
@MappedSuperclass
public abstract class JbSession extends JbVerifier {

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("用户编号")
    private Long userId;

    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    @JaLang("用户名")
    private String username;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "地址")
    private String address;

    @JaEdit(types = "ip", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "IP")
    private long ip;

    @JaLang(value = "设备", tag = "device")
    private String agent;

    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaLang(value = "最后登录", tag = "lastLogin")
    private long lastTime;

    @JaLang(value = "不可用")
    private boolean disable;

    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Map<String, String> getMetaMap() {
        return metaMap;
    }

    public void setMetaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
    }

    public Object getMetaMap(String key) {
        return metaMap == null ? null : metaMap.get(key);
    }

    public void setMetaMap(String key, String value) {
        if (metaMap == null) {
            metaMap = new HashMap<String, String>();
        }

        metaMap.put(key, value);
    }
}
