/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-23 下午3:27:52
 */
package com.absir.open.bean.value;

import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
public enum JePayStatus {

	@JaLang("付款中") PAYING,

	@JaLang("已付款") PAYED,

	@JaLang("交易错误") ERROR,

	@JaLang("交易完成") COMPLETE,

	@JaLang("交易关闭") CLOSED,
}
