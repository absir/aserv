/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-24 下午3:00:55
 */
package com.absir.orm.value;

import java.io.Serializable;

import javax.persistence.Id;

/**
 * @author absir
 * 
 */
public interface JiAssoc {

	/**
	 * 主键ID
	 * 
	 * @return
	 */
	@Id
	public Long getId();

	/**
	 * 获取关联实体主键
	 * 
	 * @return
	 */
	public Serializable getAssocId();
}
