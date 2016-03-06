/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月9日 下午2:57:07
 */
package com.absir.aserv.system.bean;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.absir.aserv.system.bean.base.JbBeanSL;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@Entity
public class JUploadCite extends JbBeanSL {

	@JaLang(value = "上传内容", tag = "uploadContent")
	@ManyToOne(fetch = FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private JUpload upload;

	/**
	 * @return the upload
	 */
	public JUpload getUpload() {
		return upload;
	}

	/**
	 * @param upload
	 *            the upload to set
	 */
	public void setUpload(JUpload upload) {
		this.upload = upload;
	}
}
