/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月17日 上午10:46:07
 */
package com.absir.log.bean;

import javax.persistence.Entity;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@Entity
public class JPayLog extends JbLog {

	@JaLang("金额")
	private int amount;

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
