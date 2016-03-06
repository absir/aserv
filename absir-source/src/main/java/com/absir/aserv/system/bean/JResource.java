/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-25 上午10:35:58
 */
package com.absir.aserv.system.bean;

import javax.persistence.Entity;

import com.absir.orm.value.JaEntity;
import com.absir.orm.value.JePermission;

/**
 * @author absir
 * 
 */
@JaEntity(permissions = JePermission.SELECT)
@Entity
public class JResource extends JbResource {

	/** fileMd5 */
	private String fileMd5;

	/**
	 * @return the fileMd5
	 */
	public String getFileMd5() {
		return fileMd5;
	}

	/**
	 * @param fileMd5
	 *            the fileMd5 to set
	 */
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
}
