/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanTargets extends JbBean {

	@JaLang("目标")
	@JaName("JSlaveServer")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	@Column(length = 10240)
	private long[] targets;

	@JaLang("描述")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String description;

	/**
	 * @return the targets
	 */
	public long[] getTargets() {
		return targets;
	}

	/**
	 * @param targets
	 *            the targets to set
	 */
	public void setTargets(long[] targets) {
		this.targets = targets;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
