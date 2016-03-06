/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-20 下午2:40:38
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.JEmbedSL;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbBeanSL extends JbBase {

	@JaLang("编号")
	@Id
	private JEmbedSL id;

	/**
	 * @return the id
	 */
	public JEmbedSL getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(JEmbedSL id) {
		this.id = id;
	}
}
