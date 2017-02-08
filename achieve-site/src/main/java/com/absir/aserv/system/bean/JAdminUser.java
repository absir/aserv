package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbUser;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.proxy.JiUserRole;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeUserType;
import com.absir.property.value.Prop;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by absir on 2017/1/13.
 */
@MaEntity(parent = {@MaMenu("用户管理")}, name = "管理员")
@Entity
public class JAdminUser extends JbUser implements JiUserBase {

    @JaLang("用户名")
    @Prop(include = 1)
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @Column(unique = true)
    private String username;

    @JaLang("用户角色")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<JUserRole> userRoles;

    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public Long getUserId() {
        return getId();
    }

    @Override
    public int getUserRoleLevel() {
        return JeUserType.USER_ADMIN.ordinal();
    }

    @Override
    public Collection<? extends JiUserRole> userRoles() {
        return getUserRoles();
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
