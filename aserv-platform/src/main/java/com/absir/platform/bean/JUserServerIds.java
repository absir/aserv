package com.absir.platform.bean;

import com.absir.aserv.system.bean.base.JbBeanL;

import javax.persistence.Entity;

@Entity
public class JUserServerIds extends JbBeanL {

    private long lastServerId;

    //@Type(type = "com.absir.aserv.system.bean.type.JtJsonArray")
    private String serverIds;

    public long getLastServerId() {
        return lastServerId;
    }

    public void setLastServerId(long lastServerId) {
        this.lastServerId = lastServerId;
    }

    public String getServerIds() {
        return serverIds;
    }

    public void setServerIds(String serverIds) {
        this.serverIds = serverIds;
    }

}
