/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-10 下午11:30:34
 */
package com.absir.master.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.property.value.Prop;

/**
 * @author absir
 * 
 */
@SuppressWarnings("serial")
@MaEntity(parent = { @MaMenu("账号管理") }, name = "注册")
@Entity
public class JRegister extends JbBase implements Serializable {

	@JaLang(value = "用户名", tag = "username")
	@Id
	private String id;

	@JaLang("密码")
	@Prop(include = 1)
	@JaEdit(editable = JeEditable.OPTIONAL, types = "password")
	@JaCrud(cruds = { Crud.CREATE, Crud.UPDATE }, factory = PasswordCrudFactory.class)
	@Column(columnDefinition = "char(32)")
	private String password;

	@JaLang(value = "加密", tag = "encryption")
	@Prop(include = 99)
	@JaEdit(editable = JeEditable.DISABLE)
	private String salt;

	@JaLang("邮箱")
	private String email;

	@JaLang("平台用户编号")
	private long platformUserId;

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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * @param salt
	 *            the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the platformUserId
	 */
	public long getPlatformUserId() {
		return platformUserId;
	}

	/**
	 * @param platformUserId
	 *            the platformUserId to set
	 */
	public void setPlatformUserId(long platformUserId) {
		this.platformUserId = platformUserId;
	}

}
