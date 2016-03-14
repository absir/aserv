/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.bean.value.JiActive;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanTargetsO extends JbBeanTargets implements JiActive {

    @JaLang("服务区")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private long serverId;

    @JaLang("开启编号")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private long openId;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang("结束时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long passTime;

    /**
     * @return the serverId
     */
    public long getServerId() {
        return serverId;
    }

    /**
     * @param serverId the serverId to set
     */
    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    /**
     * @return the openId
     */
    public long getOpenId() {
        return openId;
    }

    /**
     * @param openId
     *            the openId to set
     */
    public void setOpenId(long openId) {
        this.openId = openId;
    }

    /**
     * @return the beginTime
     */
    public long getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime
     *            the beginTime to set
     */
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the passTime
     */
    public long getPassTime() {
        return passTime;
    }

    /**
     * @param passTime
     *            the passTime to set
     */
    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }
}
