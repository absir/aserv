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

@MappedSuperclass
public class JbServerTargetsO extends JbServerTargets implements JiActive {

    @JaLang("服务编号")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private long serverId;

    @JaLang(value = "开启编号", tag = "openId")
    @JsonIgnore
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private long serversOpenId;

    @JaLang("开始时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long beginTime;

    @JaLang("结束时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    private long passTime;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getServersOpenId() {
        return serversOpenId;
    }

    public void setServersOpenId(long serversOpenId) {
        this.serversOpenId = serversOpenId;
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
}
