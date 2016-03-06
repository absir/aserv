/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年8月26日 下午2:42:42
 */
package com.absir.aserv.lang;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JeEditable;

/**
 * @author absir
 *
 */
public interface ILangBase {

	/**
	 * @param fieldName
	 * @param locale
	 * @param type
	 * @return
	 */
	public <T> T getLang(String fieldName, Integer locale, Class<T> type);

	/**
	 * @param fieldName
	 * @param locale
	 * @param value
	 */
	public void setLang(String fieldName, Integer locale, Object value);

	/**
	 * @param value
	 */
	@JaEdit(editable = JeEditable.DISABLE)
	public void setLangValues(String[] values);

}
