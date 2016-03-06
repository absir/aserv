/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.value;

/**
 * @author absir
 * 
 */
public enum JeUserType {

	@JaLang("禁用")
	USER_BAN,

	@JaLang("访客")
	USER_GUEST,

	@JaLang("验证用户")
	USER_VALIDATING,

	@JaLang("普通用户")
	USER_NORMAL,

	@JaLang("管理员")
	USER_ADMIN, ;
}
