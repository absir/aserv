/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-20 下午2:41:30
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbBeanSS extends JbBase {

	@JaLang("编号")
	@Id
	private JEmbedSS id;

	/**
	 * @return the id
	 */
	public JEmbedSS getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(JEmbedSS id) {
		this.id = id;
	}

}
