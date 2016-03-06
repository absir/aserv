/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.orm.value;

import com.absir.core.util.UtilEnum.EnumInteger;

/**
 * @author absir
 * 
 */
public enum JePermission implements EnumInteger {

	/**
	 * 访问授权
	 */
	SELECT(0X01),

	/**
	 * 插入授权
	 */
	INSERT(0X02),

	/**
	 * 更新授权
	 */
	UPDATE(0X04),

	/**
	 * 删除授权
	 */
	DELETE(0X08),

	;

	/** value */
	private int value;

	/**
	 * @param value
	 */
	JePermission(int value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.util.UtilEnum.EnumObject#getValue()
	 */
	public Integer getValue() {
		return value;
	}
}
