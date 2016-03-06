/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月27日 下午12:18:52
 */
package com.absir.aserv.system.bean;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Index;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.service.BeanService;
import com.absir.context.core.ContextUtils;
import com.absir.orm.value.JaColum;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("系统管理") }, name = "日志")
@JaModel(desc = true)
@Entity
public class JLog extends JbBean {

	@JaLang("名称")
	@JaColum(indexs = @Index(name = "name", columnList = "name"))
	private String name;

	@JaLang("动作")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String action;

	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	private long createTime;

	@JaLang("IP")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String ip;

	@JaLang("用户名")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String username;

	@JaLang("成功")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private boolean success;

	/**
	 * @param log
	 */
	public static void log(JLog log) {
		BeanService.ME.persist(log);
	}

	/**
	 * @param name
	 * @param action
	 * @param ip
	 * @param username
	 * @param success
	 * @return
	 */
	public static JLog log(String name, String action, String ip, String username, boolean success) {
		JLog log = new JLog(name, action, ip, username, success);
		log(log);
		return log;
	}

	/**
	 * @param logs
	 */
	public static void logs(Collection<JLog> logs) {
		BeanService.ME.persists(logs);
	}

	/**
	 * 
	 */
	public JLog() {
	}

	/**
	 * @param name
	 * @param action
	 * @param ip
	 * @param username
	 * @param success
	 */
	public JLog(String name, String action, String ip, String username, boolean success) {
		this.name = name;
		this.action = action;
		createTime = ContextUtils.getContextTime();
		this.ip = ip;
		this.username = username;
		this.success = success;
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
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
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
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
}
