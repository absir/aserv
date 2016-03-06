/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月16日 上午10:23:13
 */
package com.absir.platform.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Index;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaColum;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("平台管理") }, name = "用户")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformUser extends JbBean implements JiUserBase {

	@JaLang("平台")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	@JaColum(indexs = @Index(columnList = "platform,username", unique = true) )
	private String platform;

	@JaLang("用户名")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	private String username;

	@JaLang("渠道")
	@JaEdit(groups = { JaEdit.GROUP_SUGGEST })
	private String channel;

	@JaLang("禁用")
	@JaEdit(groups = { JaEdit.GROUP_LIST })
	private boolean disabled;

	@JaLang("服务区")
	@JaEdit(groups = { JaEdit.GROUP_LIST })
	private long serverId;

	@JaLang("角色ID")
	@JaEdit(groups = { JaEdit.GROUP_LIST })
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

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * @param platform
	 *            the platform to set
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled
	 *            the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the playerId
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId
	 *            the playerId to set
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return the serverId
	 */
	public long getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(long serverId) {
		this.serverId = serverId;
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

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the metaMap
	 */
	public Map<String, String> getMetaMap() {
		return metaMap;
	}

	/**
	 * @param metaMap
	 *            the metaMap to set
	 */
	public void setMetaMap(Map<String, String> metaMap) {
		this.metaMap = metaMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#getUserId()
	 */
	@Override
	public Long getUserId() {
		return getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#isDeveloper()
	 */
	@Override
	public boolean isDeveloper() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#isActivation()
	 */
	@Override
	public boolean isActivation() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#getUserRoleLevel()
	 */
	@Override
	public int getUserRoleLevel() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#userRoles()
	 */
	@Override
	public Collection<? extends JiUserRole> userRoles() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#getUserRoles()
	 */
	@Override
	public Collection<? extends JbUserRole> getUserRoles() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#getMetaMap(java.lang.
	 * String)
	 */
	@Override
	public Object getMetaMap(String key) {
		return metaMap == null ? null : metaMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JiUserBase#setMetaMap(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void setMetaMap(String key, String value) {
		if (metaMap == null) {
			metaMap = new HashMap<String, String>();
		}

		metaMap.put(key, value);
	}

}
