package com.absir.master.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.orm.value.JaColum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Index;

/**
 * Created by absir on 16/3/23.
 */
@Entity
public class JPlayer extends JbBean {

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

    @JaLang("平台")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String platform;

    @JaLang("渠道")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private String channel;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime")
    private long createTime;

    @JaLang("逻辑编号")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private String logicId;

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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getLogicId() {
        return logicId;
    }

    public void setLogicId(String logicId) {
        this.logicId = logicId;
    }
}
