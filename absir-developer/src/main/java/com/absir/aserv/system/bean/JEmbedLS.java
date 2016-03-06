/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-1 下午3:36:44
 */
package com.absir.aserv.system.bean;

import javax.persistence.Embeddable;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiEmbed;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class JEmbedLS implements JiEmbed {

	/** eid */
	@JaEdit(groups = { JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST })
	@JaLang("编号")
	private Long eid;

	/** mid */
	@JaEdit(groups = { JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST })
	@JaLang("关联")
	private String mid;

	/**
	 * 
	 */
	public JEmbedLS() {

	}

	/**
	 * @param eid
	 * @param mid
	 */
	public JEmbedLS(Long eid, String mid) {
		this.eid = eid;
		this.mid = mid;
	}

	/**
	 * @return the eid
	 */
	public Long getEid() {
		return eid;
	}

	/**
	 * @param eid
	 *            the eid to set
	 */
	public void setEid(Long eid) {
		this.eid = eid;
	}

	/**
	 * @return the mid
	 */
	public String getMid() {
		return mid;
	}

	/**
	 * @param mid
	 *            the mid to set
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return KernelObject.hashCode(eid) + KernelObject.hashCode(mid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof JEmbedLS) {
			JEmbedLS target = (JEmbedLS) obj;
			return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid);
		}

		return false;
	}

}
