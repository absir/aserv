/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年5月6日 上午9:28:38
 */
package com.absir.aserv.master.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanSS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.orm.value.JaColum;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("节点管理") }, name = "同步", value = @MaMenu(order = -127) )
@JaModel(desc = true)
@Entity
public class JSlaveSynch extends JbBeanSS {

	@JaLang("链接")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String uri;

	@JaLang("提交数据")
	@Lob
	@Column(length = 10240)
	private byte[] postData;

	@JaLang("更新时间")
	@JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime", editable = JeEditable.LOCKED)
	private long updateTime;

	@JaLang("已经同步")
	@JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
	@JaColum(indexs = @Index(columnList = "synched") )
	private boolean synched;

	@JaLang("节点自动同步")
	@JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
	private boolean slaveAutoSynch;

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the postData
	 */
	public byte[] getPostData() {
		return postData;
	}

	/**
	 * @param postData
	 *            the postData to set
	 */
	public void setPostData(byte[] postData) {
		this.postData = postData;
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

	/**
	 * @return the synched
	 */
	public boolean isSynched() {
		return synched;
	}

	/**
	 * @param synched
	 *            the synched to set
	 */
	public void setSynched(boolean synched) {
		this.synched = synched;
	}

	/**
	 * @return the slaveAutoSynch
	 */
	public boolean isSlaveAutoSynch() {
		return slaveAutoSynch;
	}

	/**
	 * @param slaveAutoSynch
	 *            the slaveAutoSynch to set
	 */
	public void setSlaveAutoSynch(boolean slaveAutoSynch) {
		this.slaveAutoSynch = slaveAutoSynch;
	}

}
