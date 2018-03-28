/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-10 下午11:30:34
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbUser;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JpMeta;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.core.kernel.KernelString;
import com.absir.property.value.Prop;
import com.absir.validator.value.Email;
import com.absir.validator.value.Regex;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@MaEntity(parent = {@MaMenu("用户管理")}, name = "用户")
@Entity
public class JUser extends JbUser implements IUser, JiUserBase, JiRoleLevel, JpMeta, Serializable {

    @JaLang("用户名")
    @Prop(include = 1)
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @Column(unique = true)
    @Regex(value = "^[^\\d@][^@]{0,32}$", lang = "请输入首位不是数字,不含有@的0-32位字符")
    private String username;

    @JaLang("邮箱")
    @Prop(include = 1)
    @Email
    private String email;

    @JaLang("手机")
    @Prop(include = 1)
    @Regex(value = "^\\d*$")
    private String mobile;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long createTime;

    @JaLang("昵称")
    private String nickname;

    @JaLang("用户类型")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @Enumerated
    private JeUserType userType;

    @JaLang("用户角色")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<JUserRole> userRoles;

    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

    @JaLang("密码")
    @Prop(include = 1)
    @JaEdit(types = "password")
    @JaCrud(cruds = {Crud.CREATE, Crud.UPDATE}, factory = PasswordCrudFactory.class)
    @Column(columnDefinition = "char(32)")
    private String password;

    @Transient
    @JaEdit(editable = JeEditable.LOCKNONE)
    private String passwordBase;

    @JaLang(value = "加密", tag = "encryption")
    @JaEdit(editable = JeEditable.DISABLE)
    private String salt;

    @JaLang(value = "加密次数", tag = "encryptionCount")
    @JaEdit(editable = JeEditable.DISABLE)
    private int saltCount;

    @JaLang("最后登录")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long lastLogin;

    @JaLang("错误登录")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int errorLogin;

    @JaLang("最后错误登录")
    @Prop(include = 99)
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long lastErrorLogin;

    @JaLang("登录次数")
    @Prop(include = 99)
    private int loginTimes;

    @JaLang("登录地址")
    @Prop(include = 99)
    private String loginAddress;

    @JaLang("剩余错误次数")
    @Prop(include = 99)
    private int lastErrorTimes;

    @JaLang("沉默")
    @Transient
    private transient boolean silent;

    public Long getUserId() {
        return getId();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getNickname() {
        if (KernelString.isEmpty(nickname)) {
            if (!KernelString.isEmpty(username)) {
                nickname = username;

            } else if (getId() != null) {
                nickname = "User." + getId();
            }
        }

        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public JeUserType getUserType() {
        return userType;
    }

    public void setUserType(JeUserType userType) {
        this.userType = userType;
    }

    public Set<JUserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<JUserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Map<String, String> getMetaMap() {
        return metaMap;
    }

    public void setMetaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
    }

    // 用户角色
    public Set<JUserRole> userRoles() {
        return userRoles;
    }

    // 角色等级
    public int getUserRoleLevel() {
        return userType == null ? -1 : userType.ordinal();
    }

    // 角色等级
    public JeRoleLevel getJeRoleLevel() {
        if (isDeveloper()) {
            return JeRoleLevel.ROLE_DEVELOPER;
        }

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

    public Object getMetaMap(String key) {
        return metaMap == null ? null : metaMap.get(key);
    }

    public void setMetaMap(String key, String value) {
        if (metaMap == null) {
            metaMap = new HashMap<String, String>();
        }

        metaMap.put(key, value);
    }

    @Override
    public String toString() {
        return "JUser[" + getId() + "]." + username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordBase() {
        return passwordBase;
    }

    public void setPasswordBase(String passwordBase) {
        this.passwordBase = passwordBase;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getSaltCount() {
        return saltCount;
    }

    public void setSaltCount(int saltCount) {
        this.saltCount = saltCount;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getErrorLogin() {
        return errorLogin;
    }

    public void setErrorLogin(int errorLogin) {
        this.errorLogin = errorLogin;
    }

    public long getLastErrorLogin() {
        return lastErrorLogin;
    }

    public void setLastErrorLogin(long lastErrorLogin) {
        this.lastErrorLogin = lastErrorLogin;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    public int getLastErrorTimes() {
        return lastErrorTimes;
    }

    public void setLastErrorTimes(int lastErrorTimes) {
        this.lastErrorTimes = lastErrorTimes;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

}
