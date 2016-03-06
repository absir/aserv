/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 下午2:29:14
 */
package com.absir.master.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.NotEmpty;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("平台管理") }, name = "渠道", value = @MaMenu(order = -128) )
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JChannel extends JbBase {

	@JaLang(value = "渠道", tag = "channel")
	@Id
	@NotEmpty
	private String id;

	@JaLang("名称")
	private String name;

	@JaLang("公告别名")
	private String announcementAlias;

	@JaLang("服务别名")
	private String serverAlias;

	@JaLang("版本")
	private String version;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the announcementAlias
	 */
	public String getAnnouncementAlias() {
		return announcementAlias;
	}

	/**
	 * @param announcementAlias
	 *            the announcementAlias to set
	 */
	public void setAnnouncementAlias(String announcementAlias) {
		this.announcementAlias = announcementAlias;
	}

	/**
	 * @return the serverAlias
	 */
	public String getServerAlias() {
		return serverAlias;
	}

	/**
	 * @param serverAlias
	 *            the serverAlias to set
	 */
	public void setServerAlias(String serverAlias) {
		this.serverAlias = serverAlias;
	}

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

}
