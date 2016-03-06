/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月4日 下午7:37:02
 */
package com.absir.aserv.system.bean;

import javax.persistence.Entity;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 *
 */
@Entity
public class JVersion extends JbBean {

	@JaEdit(groups = JaEdit.GROUP_LIST)
	@JaLang(value = "版本")
	private String version;

	@JaEdit(groups = JaEdit.GROUP_LIST)
	@JaLang(value = "版本名")
	private String versionName;

	@JaEdit(groups = JaEdit.GROUP_LIST)
	@JaLang(value = "版本文件")
	private String versionFile;

	@JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
	@JaLang(value = "创建时间")
	private long createTime;

	@JaLang(value = "描述")
	private String description;

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the versionName
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * @param versionName
	 *            the versionName to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	/**
	 * @return the versionFile
	 */
	public String getVersionFile() {
		return versionFile;
	}

	/**
	 * @param versionFile
	 *            the versionFile to set
	 */
	public void setVersionFile(String versionFile) {
		this.versionFile = versionFile;
	}

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
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
