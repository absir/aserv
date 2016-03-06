/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月17日 上午11:12:46
 */
package com.absir.log.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
public class JServerLog extends JbBean {

	@JaLang("创建时间")
	@JaEdit(types = "dateTime")
	private long createTime;

	@JaLang("区编号")
	private long serverId;

	@JaLang("全部玩家")
	private int allPlayer;

	@JaLang("新增玩家")
	private int newPlayer;

	@JaLang("活跃玩家")
	private int loginPlayer;

	@JaLang("付费玩家")
	private int payPlayer;

	@JaLang("全部充值")
	private float totalPay;

	@JaLang("最低在线")
	private int minOnline;

	@JaLang("最高在线")
	private int maxOnline;

	@JaLang("平均在线")
	private int onlineNumber;

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
	 * @return the allPlayer
	 */
	public int getAllPlayer() {
		return allPlayer;
	}

	/**
	 * @param allPlayer
	 *            the allPlayer to set
	 */
	public void setAllPlayer(int allPlayer) {
		this.allPlayer = allPlayer;
	}

	/**
	 * @return the newPlayer
	 */
	public int getNewPlayer() {
		return newPlayer;
	}

	/**
	 * @param newPlayer
	 *            the newPlayer to set
	 */
	public void setNewPlayer(int newPlayer) {
		this.newPlayer = newPlayer;
	}

	/**
	 * @return the loginPlayer
	 */
	public int getLoginPlayer() {
		return loginPlayer;
	}

	/**
	 * @param loginPlayer
	 *            the loginPlayer to set
	 */
	public void setLoginPlayer(int loginPlayer) {
		this.loginPlayer = loginPlayer;
	}

	/**
	 * @return the payPlayer
	 */
	public int getPayPlayer() {
		return payPlayer;
	}

	/**
	 * @param payPlayer
	 *            the payPlayer to set
	 */
	public void setPayPlayer(int payPlayer) {
		this.payPlayer = payPlayer;
	}

	/**
	 * @return the totalPay
	 */
	public float getTotalPay() {
		return totalPay;
	}

	/**
	 * @param totalPay
	 *            the totalPay to set
	 */
	public void setTotalPay(float totalPay) {
		this.totalPay = totalPay;
	}

	/**
	 * @return the minOnline
	 */
	public int getMinOnline() {
		return minOnline;
	}

	/**
	 * @param minOnline
	 *            the minOnline to set
	 */
	public void setMinOnline(int minOnline) {
		this.minOnline = minOnline;
	}

	/**
	 * @return the maxOnline
	 */
	public int getMaxOnline() {
		return maxOnline;
	}

	/**
	 * @param maxOnline
	 *            the maxOnline to set
	 */
	public void setMaxOnline(int maxOnline) {
		this.maxOnline = maxOnline;
	}

	/**
	 * @return the onlineNumber
	 */
	public int getOnlineNumber() {
		return onlineNumber;
	}

	/**
	 * @param onlineNumber
	 *            the onlineNumber to set
	 */
	public void setOnlineNumber(int onlineNumber) {
		this.onlineNumber = onlineNumber;
	}

}
