/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 上午10:23:13
 */
package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaColum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Index;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "用户")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformUser extends JbBean implements JiUserBase {

    @JaLang("平台")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    @JaColum(indexs = @Index(columnList = "platform,username", unique = true))
    private String platform;

    @JaLang("用户名")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String username;

    @JaLang("渠道")
    @JaEdit(groups = {JaEdit.GROUP_SUGGEST})
    private String channel;

    @JaLang("昵称")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private String nickname;

    @JaLang("禁用")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private boolean disabled;

    @JaLang("服务区")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private long serverId;

    @JaLang("角色ID")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    private Long playerId;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime")
    private long passTime;

    @JaLang("会话编号")
    private String sessionId;

    /**
     * @author absir 扩展存储
     */
    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, String> getMetaMap() {
        return metaMap;
    }

    public void setMetaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
    }

    @Override
    public Long getUserId() {
        return getId();
    }

    @Override
    public boolean isDeveloper() {
        return false;
    }

    @Override
    public boolean isActivation() {
        return true;
    }

    @Override
    public int getUserRoleLevel() {
        return 0;
    }

    @Override
    public Collection<? extends JiUserRole> userRoles() {
        return null;
    }

    @Override
    public Collection<? extends JbUserRole> getUserRoles() {
        return null;
    }

    @Override
    public Object getMetaMap(String key) {
        return metaMap == null ? null : metaMap.get(key);
    }

    @Override
    public void setMetaMap(String key, String value) {
        if (metaMap == null) {
            metaMap = new HashMap<String, String>();
        }

        metaMap.put(key, value);
    }

}
