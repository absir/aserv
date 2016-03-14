/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-25 上午11:46:27
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbResource extends JbBase implements JiUpdate {

    @Id
    String id;

    @JsonIgnore
    private long updateTime;

    @JsonIgnore
    private boolean scanned;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the updateTime
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     *            the updateTime to set
     */
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the scanned
     */
    public boolean isScanned() {
        return scanned;
    }

    /**
     * @param scanned
     *            the scanned to set
     */
    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }
}
