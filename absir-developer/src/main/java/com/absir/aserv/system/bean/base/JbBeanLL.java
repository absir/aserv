/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-20 下午2:38:37
 */
package com.absir.aserv.system.bean.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.JEmbedLL;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 * 
 */
@MappedSuperclass
public class JbBeanLL extends JbBase {

	@JaLang("编号")
	@Id
	private JEmbedLL id;

	/**
	 * @return the id
	 */
	public JEmbedLL getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(JEmbedLL id) {
		this.id = id;
	}
}
