/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年7月15日 上午9:36:45
 */
package com.absir.aserv.system.crud.value;

import java.io.InputStream;

/**
 * @author absir
 *
 */
public interface IUploadRule {

	/**
	 * @param name
	 * @param extensionName
	 * @return
	 */
	public String getUploadRuleName(String name, String extensionName);

	/**
	 * @param name
	 * @param inputStream
	 * @param extensionName
	 * @return
	 */
	public InputStream proccessInputStream(String name, InputStream inputStream, String extensionName);
}
