/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-12 下午2:05:05
 */
package com.absir.open.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.context.core.ContextUtils;
import com.absir.orm.value.JaColum;

/**
 * @author absir
 * 
 */
@MaEntity(parent = { @MaMenu("支付管理") }, name = "支付")
@Entity
public class JPayHistory extends JbBase {

	/** id */
	@JaLang(value = "订单号", tag = "tradeId")
	@Id
	private String id;

	/** platform */
	@JaLang("平台")
	@JaColum(indexs = @Index(columnList = "platform,tradeNo", unique = true) )
	private String platform;

	/** tradeNo */
	@JaLang("交易号")
	private String tradeNo;

	/** createTime */
	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	private long createTime = ContextUtils.getContextTime();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * @param platform
	 *            the platform to set
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo
	 *            the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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
}
