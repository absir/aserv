package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBeanL;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

@Entity
public class JRolePermissions extends JbBeanL {

    @JaLang("角色名称")
    @Transient
    @JaEdit(editable = JeEditable.LOCKED)
    private String rolename;

    @JaLang("实体权限")
    @JaEdit(editable = JeEditable.DISABLE)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private Map<String, JPermission> maPermissions;

    public static class DPermission {

        @JaLang("编号")
        public String id;

        @JaLang("实体权限")
        JPermission permission;

        @JaLang("授权")
        public boolean authorize;
    }

    @JaLang("权限编辑")
    private transient List<DPermission> permissions;

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Map<String, JPermission> getMaPermissions() {
        return maPermissions;
    }

    public void setMaPermissions(Map<String, JPermission> maPermissions) {
        this.maPermissions = maPermissions;
    }

    public List<DPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DPermission> permissions) {
        this.permissions = permissions;
    }

}
