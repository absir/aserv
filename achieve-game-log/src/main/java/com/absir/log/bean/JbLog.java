/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月17日 上午10:47:14
 */
package com.absir.log.bean;

import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbLog extends JbBean {

	@JaLang("创建时间")
	@JaEdit(types = "dateTime")
	private long createTime;

	@JaLang("平台")
	private String platform;

	@JaLang("渠道")
	private String channel;

	@JaLang("区编号")
	private long serverId;

	@JaLang("角色编号")
	private long playerId;

	@JaLang("设备编号")
	private String deviceId;

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

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
	 * @return the playerId
	 */
	public long getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId
	 *            the playerId to set
	 */
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

}
