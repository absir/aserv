package com.absir.platform.bean;

import com.absir.aserv.system.bean.base.JbVerifier;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * Created by absir on 16/3/23.
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformSession extends JbVerifier {

    @JaLang("平台用户编号")
    private long platformUserId;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "地址")
    private String address;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "设备", tag = "device")
    private String agent;

    public long getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(long platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
