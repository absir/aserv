/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-12 下午2:05:05
 */
package com.absir.open.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.open.bean.value.JePayStatus;

/**
 * @author absir
 * 
 */
@MaEntity(parent = { @MaMenu("支付管理") }, name = "订单")
@Entity
public class JPayTrade extends JbBase {

	/** id */
	@JaLang(value = "订单号", tag = "tradeId")
	@Id
	private String id;

	/** createTime */
	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	private long createTime;

	/** platform */
	@JaLang(value = "平台名称", tag = "platformName")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String platform;

	/** platformData */
	@JaLang("平台参数")
	private String platformData;

	/** tradeNo */
	@JaLang("交易号")
	private String tradeNo;

	/** tradeNo */
	@JaLang("交易号")
	@Column(length = 1024)
	private String tradeData;

	/** channel */
	@JaLang("渠道")
	private String channel;

	/** status */
	@JaLang(value = "交易状态", tag = "tradeStatus")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private JePayStatus status;

	@JaLang(value = "交易状态参数", tag = "tradeStatusData")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String statusData;

	/** userId */
	@JaLang("用户编号")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private long userId;

	/** serverId */
	@JaLang("服务编号")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private long serverId;

	/** name */
	@JaLang(value = "商品名", tag = "goodsName")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String name;

	/** nameId */
	@JaLang(value = "商品编号", tag = "goodsId")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private int nameId;

	/** nameData */
	@JaLang(value = "商品参数", tag = "goodsData")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String nameData;

	/** amount */
	@JaLang("金额")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private float amount;

	/** source */
	@JaLang(value = "资源")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String source;

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
	 * @return the platformData
	 */
	public String getPlatformData() {
		return platformData;
	}

	/**
	 * @param platformData
	 *            the platformData to set
	 */
	public void setPlatformData(String platformData) {
		this.platformData = platformData;
	}

	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo
	 *            the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	/**
	 * @return the tradeData
	 */
	public String getTradeData() {
		return tradeData;
	}

	/**
	 * @param tradeData
	 *            the tradeData to set
	 */
	public void setTradeData(String tradeData) {
		this.tradeData = tradeData;
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
	 * @return the status
	 */
	public JePayStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(JePayStatus status) {
		this.status = status;
	}

	/**
	 * @return the statusData
	 */
	public String getStatusData() {
		return statusData;
	}

	/**
	 * @param statusData
	 *            the statusData to set
	 */
	public void setStatusData(String statusData) {
		this.statusData = statusData;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
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
	 * @return the nameId
	 */
	public int getNameId() {
		return nameId;
	}

	/**
	 * @param nameId
	 *            the nameId to set
	 */
	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	/**
	 * @return the nameData
	 */
	public String getNameData() {
		return nameData;
	}

	/**
	 * @param nameData
	 *            the nameData to set
	 */
	public void setNameData(String nameData) {
		this.nameData = nameData;
	}

	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

}
