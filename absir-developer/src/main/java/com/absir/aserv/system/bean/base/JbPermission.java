/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.proxy.JiUser;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbPermission extends JbAssoc implements JiUser {

	@JaLang("关联用户")
	private Long userId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JpUser#getUserId()
	 */
	@Override
	public Long getUserId() {
		return userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.proxy.JpUser#setUserId(java.lang.Long)
	 */
	@Override
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
