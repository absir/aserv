/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-30 下午7:27:41
 */
package com.absir.server.exception;

/**
 * @author absir
 * 
 */
public enum ServerStatus {

	/** IN_FAILED */
	IN_FAILED(0),

	/** ON_SUCCESS */
	ON_SUCCESS(200),

	/** ON_CODE */
	ON_CODE(201),

	/** ON_DELETED */
	ON_DELETED(204),

	/** ON_FAIL */
	ON_FAIL(205),

	/** NO_USER */
	NO_USER(220),

	/** NO_VERIFY */
	NO_VERIFY(221),

	/** ON_TIMEOUT */
	ON_TIMEOUT(222),

	/** ON_DENIED */
	ON_DENIED(304),

	/** ON_ERROR */
	ON_ERROR(400),

	/** NO_LOGIN */
	NO_LOGIN(402),

	/** ON_FORBIDDEN */
	ON_FORBIDDEN(403),

	/** IN_404 */
	IN_404(404),

	/** IN_405 */
	IN_405(405),

	/** NO_PARAM */
	NO_PARAM(437),

	;

	/** code */
	private int code;

	/**
	 * @param code
	 */
	ServerStatus(int code) {
		this.code = code;
	}

	/**
	 * @return
	 */
	public int getCode() {
		return code;
	}
}
