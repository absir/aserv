/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月10日 下午1:17:43
 */
package com.absir.aserv.system.crud.value;

import java.io.InputStream;

import com.absir.aserv.system.bean.JUpload;

/**
 * @author absir
 *
 */
public interface IUploadProcessor {

	/**
	 * @param upload
	 * @param inputStream
	 * @return
	 */
	public InputStream process(JUpload upload, InputStream inputStream);

}
