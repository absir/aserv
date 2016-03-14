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
import com.absir.orm.value.JaColum;
import com.absir.property.value.Prop;
import com.absir.validator.value.Email;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author absir
 */
@SuppressWarnings("serial")
@MaEntity(parent = {@MaMenu("用户管理")}, name = "用户")
@Entity
public class JUser extends JbUser implements IUser, JiUserBase, JiRoleLevel, JpMeta, Serializable {

    @JaLang("用户名")
    @Prop(include = 1)
    @JaColum(indexs = @Index(name = "username", columnList = "username"))
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @Column(unique = true, length = 64)
    private String username;

    @JaLang("用户类型")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @Enumerated
    private JeUserType userType;

    @JaLang("用户角色")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<JUserRole> userRoles;

    /**
     * @author absir 扩展存储
     */
    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

    @JaLang("密码")
    @Prop(include = 1)
    @JaEdit(editable = JeEditable.OPTIONAL, types = "password")
    @JaCrud(cruds = {Crud.CREATE, Crud.UPDATE}, factory = PasswordCrudFactory.class)
    @Column(columnDefinition = "char(32)")
    private String password;

    @JaLang(value = "加密", tag = "encryption")
    @Prop(include = 99)
    @JaEdit(editable = JeEditable.DISABLE)
    private String salt;

    @JaLang("最后登录")
    @Prop(include = 99)
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long lastLogin;

    @JaLang("错误登录")
    @Prop(include = 99)
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int errorLogin;

    @JaLang("最后错误登录")
    @Prop(include = 99)
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long lastErrorLogin;

    @JaLang("邮箱")
    @Prop(include = 1)
    @Email
    @NotEmpty
    private String email;

    @JaLang("手机")
    @Prop(include = 1)
    private String mobile;

    @JaLang("沉默")
    @Transient
    private transient boolean slient;

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.system.bean.proxy.Proxies.JpUserBase#getUserId()
     */
    public Long getUserId() {
        return getId();
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
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
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * @return the lastLogin
     */
    public long getLastLogin() {
        return lastLogin;
    }

    /**
     * @param lastLogin the lastLogin to set
     */
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * @return the errorLogin
     */
    public int getErrorLogin() {
        return errorLogin;
    }

    /**
     * @param errorLogin the errorLogin to set
     */
    public void setErrorLogin(int errorLogin) {
        this.errorLogin = errorLogin;
    }

    /**
     * @return the lastErrorLogin
     */
    public long getLastErrorLogin() {
        return lastErrorLogin;
    }

    /**
     * @param lastErrorLogin the lastErrorLogin to set
     */
    public void setLastErrorLogin(long lastErrorLogin) {
        this.lastErrorLogin = lastErrorLogin;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the slient
     */
    public boolean isSlient() {
        return slient;
    }

    /**
     * @param slient the slient to set
     */
    public void setSlient(boolean slient) {
        this.slient = slient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
