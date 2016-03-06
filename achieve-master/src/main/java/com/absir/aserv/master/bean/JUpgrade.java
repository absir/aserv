/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月8日 下午1:48:02
 */
package com.absir.aserv.master.bean;

import javax.persistence.Entity;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("平台管理") }, name = "升级")
@JaModel(desc = true)
@Entity
public class JUpgrade extends JbBean {

	@JaLang("升级文件")
	@JaEdit(types = "file", groups = JaEdit.GROUP_LIST)
	@JaCrud(factory = UploadCrudFactory.class, parameters = "zip,war")
	private String upgradeFile;

	@JaLang("描述")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String description;

	@JaLang("开始时间")
	@JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
	private long beginTime;

	@JaLang(value = "成功")
	@JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
	private boolean success;

	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaCrud(value = "dateCrudFactory", cruds = { Crud.CREATE }, factory = DateCrudFactory.class)
	private long createTime;

	@JaLang("修改时间")
	@JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaCrud(value = "dateCrudFactory", cruds = { Crud.CREATE, Crud.UPDATE }, factory = DateCrudFactory.class)
	private long updateTime;

	/**
	 * @return the upgradeFile
	 */
	public String getUpgradeFile() {
		return upgradeFile;
	}

	/**
	 * @param upgradeFile
	 *            the upgradeFile to set
	 */
	public void setUpgradeFile(String upgradeFile) {
		this.upgradeFile = upgradeFile;
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

	/**
	 * @return the beginTime
	 */
	public long getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime
	 *            the beginTime to set
	 */
	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
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
	 * @return the updateTime
	 */
	public long getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
