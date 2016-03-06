/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月17日 上午10:45:39
 */
package com.absir.log.bean;

import javax.persistence.Entity;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@Entity
public class JLoginLog extends JbLog {

	@JaLang("新角色")
	private boolean newPlayer;

	@JaLang("新设备")
	private boolean newDevice;

	/**
	 * @return the newPlayer
	 */
	public boolean isNewPlayer() {
		return newPlayer;
	}

	/**
	 * @param newPlayer
	 *            the newPlayer to set
	 */
	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}

	/**
	 * @return the newDevice
	 */
	public boolean isNewDevice() {
		return newDevice;
	}

	/**
	 * @param newDevice
	 *            the newDevice to set
	 */
	public void setNewDevice(boolean newDevice) {
		this.newDevice = newDevice;
	}

}
