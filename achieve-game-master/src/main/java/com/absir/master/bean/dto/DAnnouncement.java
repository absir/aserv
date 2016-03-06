/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月5日 下午8:18:38
 */
package com.absir.master.bean.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@Embeddable
@MappedSuperclass
public class DAnnouncement {

	@JaLang("标题")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	public String title;

	@JaLang("内容")
	@JaEdit(types = "text")
	@Column(length = 10240)
	public String content;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

}
