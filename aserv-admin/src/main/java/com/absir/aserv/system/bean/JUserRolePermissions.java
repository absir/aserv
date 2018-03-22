package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBeanL;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaEmbedd;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

@Entity
public class JUserRolePermissions extends JbBeanL {

    @JaLang("角色名称")
    @Transient
    @JaEdit(editable = JeEditable.LOCKED)
    private String rolename;

    @JaLang("实体权限")
    @JaEdit(editable = JeEditable.DISABLE)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private Map<String, JPermission> maPermissions;

    public static class DPermission {

        public int depth;

        public JMenu menu;

        public String id;

        @JaEmbedd
        public JPermission permission;

        public boolean authorize;

    }

    @JaLang("权限编辑")
    @Transient
    //@JaEdit(editable = JeEditable.ENABLE)
    private List<DPermission> permissions;

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
