/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.absir.aserv.system.bean.JUserRole;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.bean.value.JeUserType;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.orm.value.JaColum;
import com.absir.property.value.Prop;

/**
 * @author absir
 * 
 */
public privileged aspect Proxy_Aspect_Bean {

	/**
	 * @author absir 关联实体
	 */
	@JaLang("关联主键")
	@JaColum(indexs = { @Index(name = "assocId", columnList = "assocId") })
	private Long JpAssoc.assocId;

	public Long JpAssoc.getAssocId() {
		return this.assocId;
	}

	public void JpAssoc.setAssocId(Long assocId) {
		this.assocId = assocId;
	}

	/**
	 * @author absir 创建时间
	 */
	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaCrud(value = "dateCrudFactory", cruds = { Crud.CREATE }, factory = DateCrudFactory.class)
	private long JpCreate.createTime;

	public void JpCreate.setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long JpCreate.getCreateTime() {
		return this.createTime;
	}

	/**
	 * @author absir 更新时间
	 */
	@JaLang("修改时间")
	@JaEdit(editable = JeEditable.LOCKED, types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaCrud(value = "dateCrudFactory", cruds = { Crud.CREATE, Crud.UPDATE }, factory = DateCrudFactory.class)
	private long JpUpdate.updateTime;

	public void JpUpdate.setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long JpUpdate.getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * @author absir 过期时间
	 */
	@JaLang("过期时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaColum(indexs = { @Index(name = "passTime", columnList = "passTime") })
	private long JpPass.passTime;

	public void JpPass.setPassTime(long passTime) {
		this.passTime = passTime;
	}

	public long JpPass.getPassTime() {
		return this.passTime;
	}

	@JaEdit(editable = JeEditable.DISABLE)
	@Version
	private Integer JpVersion.version;

	public void JpVersion.setVersion(Integer version) {
		this.version = version;
	}

	public Integer JpVersion.getVersion() {
		return this.version;
	}

	/**
	 * @author absir 扩展存储
	 */
	@JaLang("扩展纪录")
	@Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
	private Map<String, String> JpMeta.metaMap;

	public void JpMeta.setMetaMap(Map<String, String> metaMap) {
		this.metaMap = metaMap;
	}

	public Map<String, String> JpMeta.getMetaMap() {
		return this.metaMap;
	}

	public Object JpMeta.getMetaMap(String key) {
		Map<String, String> metaMap = this.metaMap;
		return metaMap == null ? null : metaMap.get(key);
	}

	public void JpMeta.setMetaMap(String key, String value) {
		Map<String, String> metaMap = this.metaMap;
		if (metaMap == null) {
			metaMap = new HashMap<String, String>();
		}

		metaMap.put(key, value);
	}

	/**
	 * @author absir 关联用户
	 */
	@JaLang(value = "关联用户", tag = "assocUser")
	@JaColum(indexs = @Index(name = "userId", columnList = "userId"))
	private Long JpUser.userId;

	public void JpUser.setUserId(Long userId) {
		this.userId = userId;
	}

	public Long JpUser.getUserId() {
		return this.userId;
	}

	/**
	 * @author absir 关联用户
	 */
	@JaLang(value = "关联角色", tag = "assocRole")
	@JaColum(indexs = @Index(name = "roleId", columnList = "roleId"))
	private Long JpUserRole.roleId;

	public void JpUserRole.setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long JpUserRole.getRoleId() {
		return this.roleId;
	}

	/**
	 * @author absir 用户基本
	 * 
	 */
	@JaLang("用户名")
	@Prop(include = 1)
	@JaColum(indexs = @Index(name = "username", columnList = "username"))
	@JaEdit(groups = { JaEdit.GROUP_LIST })
	@Column(unique = true, length = 64)
	private String JpUserBase.username;

	public String JpUserBase.getUsername() {
		return this.username;
	}

	public void JpUserBase.setUsername(String username) {
		this.username = username;
	}

	@JaLang("用户类型")
	@JaEdit(groups = { JaEdit.GROUP_LIST })
	@Enumerated
	private JeUserType JpUserBase.userType;

	public JeUserType JpUserBase.getUserType() {
		return this.userType;
	}

	public void JpUserBase.setUserType(JeUserType userType) {
		this.userType = userType;
	}

	// 角色等级
	public int JpUserBase.getUserRoleLevel() {
		JeUserType userType = this.userType;
		return userType == null ? -1 : userType.ordinal();
	}

	// 角色等级
	public JeRoleLevel JpUserBase.getJeRoleLevel() {
		if (isDeveloper()) {
			return JeRoleLevel.ROLE_DEVELOPER;
		}

		JeUserType userType = this.userType;
		if (userType == null) {
			return null;
		}

		if (userType.ordinal() >= JeUserType.USER_ADMIN.ordinal()) {
			return JeRoleLevel.ROLE_ADMIN;

		} else if (userType.ordinal() >= JeUserType.USER_NORMAL.ordinal()) {
			return JeRoleLevel.ROLE_USER;
		}

		return null;
	}

	@JaLang("用户角色")
	@JaEdit(groups = { JaEdit.GROUP_LIST })
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<JUserRole> JpUserBase.userRoles;

	// 用户角色
	public Set<JUserRole> JpUserBase.userRoles() {
		return this.userRoles;
	}

	// 用户角色
	public Set<JUserRole> JpUserBase.getUserRoles() {
		return this.userRoles;
	}

	public void JpUserBase.setUserRoles(Set<JUserRole> userRoles) {
		this.userRoles = userRoles;
	}

	/**
	 * JpUserBase 对象快速赋值
	 * 
	 * @param user
	 */
	public void JpUserBase.setUserBase(JpUserBase user) {
		this.username = user.username;
		this.userType = user.userType;
		this.userRoles = user.userRoles;
	}
}
