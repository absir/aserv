/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-28 上午9:26:44
 */
package com.absir.aserv.game.bean;

import com.absir.aserv.game.value.ILevelExp;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.base.JiBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.orm.value.JaColum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Index;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class JbPlayer extends JbBase implements JiBase<Long>, ILevelExp {

    @JaLang("服务区")
    @JsonIgnore
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    @JaColum(indexs = @Index(columnList = "serverId,userId"))
    private long serverId;

    @JaLang("用户ID")
    @JsonIgnore
    @JaName("JPlatformUser")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private long userId;

    @JaLang("名称")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    @JaColum(indexs = @Index(columnList = "serverId,name", unique = true))
    private String name;

    @JaLang("平台")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String platform;

    @JaLang("渠道")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private String channel;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime")
    private long createTime;

    @JaLang("禁止时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long banTime;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getBanTime() {
        return banTime;
    }

    public void setBanTime(long banTime) {
        this.banTime = banTime;
    }

}
