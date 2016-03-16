/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-28 下午6:51:32
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.proxy.JiUpdate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.io.Serializable;

@Entity
public class JUpdateXls extends JbBase implements JiUpdate {

    @EmbeddedId
    private JEmbedSS id;

    private long updateTime;

    @Lob
    private byte[] serialize;

    @Override
    public Serializable getId() {
        return id;
    }

    public void setId(JEmbedSS id) {
        this.id = id;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public byte[] getSerialize() {
        return serialize;
    }

    public void setSerialize(byte[] serialize) {
        this.serialize = serialize;
    }
}
