/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-26 下午5:25:17
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaNames;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@MaEntity(parent = {@MaMenu("用户管理")}, name = "权限")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JMaMenu extends JbBase {

    @JaLang(value = "标识", tag = "identifie")
    @Id
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String id;

    @JaLang("标题")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String caption;

    @JaLang("授权信息")
    @JaEdit(metas = "{\"key\":\"角色\"}")
    @JaNames(key = "JUserRole")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection
    private Map<Long, JPermission> permissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Langs
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Map<Long, JPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<Long, JPermission> permissions) {
        this.permissions = permissions;
    }
}
