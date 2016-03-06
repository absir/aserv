/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年4月13日 下午2:39:54
 */
package com.absir.aserv.master.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("节点管理") }, name = "节点", value = @MaMenu(order = -128) )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JSlave extends JbBase {

	@JaEdit(groups = { JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST })
	@JaLang(value = "验证主键", tag = "verifierId")
	@Id
	private String id;

	@JaLang("名称")
	@JaEdit(groups = JaEdit.GROUP_SUGGEST)
	private String name;

	@JaLang("IP")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String ip;

	@JaLang("组号")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String groupId;

	@JaLang("版本")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String version;

	@JaLang("路径")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String path;

	@JaLang("连接中")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private boolean connecting;

	@JaLang("最后连接时间")
	@JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
	private long lastConnectTime;

	@JaLang("服务IP")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String serverIP;

	@JaLang("强制开启")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private boolean forceOpen;

	@JaLang("通讯密钥")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String slaveKey;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the connecting
	 */
	public boolean isConnecting() {
		return connecting;
	}

	/**
	 * @param connecting
	 *            the connecting to set
	 */
	public void setConnecting(boolean connecting) {
		this.connecting = connecting;
	}

	/**
	 * @return the lastConnectTime
	 */
	public long getLastConnectTime() {
		return lastConnectTime;
	}

	/**
	 * @param lastConnectTime
	 *            the lastConnectTime to set
	 */
	public void setLastConnectTime(long lastConnectTime) {
		this.lastConnectTime = lastConnectTime;
	}

	/**
	 * @return the serverIP
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * @param serverIP
	 *            the serverIP to set
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * @return the forceOpen
	 */
	public boolean isForceOpen() {
		return forceOpen;
	}

	/**
	 * @param forceOpen
	 *            the forceOpen to set
	 */
	public void setForceOpen(boolean forceOpen) {
		this.forceOpen = forceOpen;
	}

	/**
	 * @return the slaveKey
	 */
	public String getSlaveKey() {
		return slaveKey;
	}

	/**
	 * @param slaveKey
	 *            the slaveKey to set
	 */
	public void setSlaveKey(String slaveKey) {
		this.slaveKey = slaveKey;
	}

}
