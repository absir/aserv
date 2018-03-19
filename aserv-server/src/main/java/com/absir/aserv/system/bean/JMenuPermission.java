/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-29 下午3:32:06
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("用户管理")}, name = "菜单权限$")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JMenuPermission extends JbBase {

    @JaLang(value = "标识", tag = "identifier")
    @Id
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String id;

    @JaLang("标题")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST}, listColType = 1)
    private String caption;

    @JaLang(value = "授权角色", tag = "allowUserRoles")
    @JaName(value = "JUserRole")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private long allowIds[];

    @JaLang(value = "禁用角色", tag = "forbidUserRoles")
    @JaName(value = "JUserRole")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private long forbidIds[];

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

    public long[] getAllowIds() {
        return allowIds;
    }

    public void setAllowIds(long[] allowIds) {
        this.allowIds = allowIds;
    }

    public long[] getForbidIds() {
        return forbidIds;
    }

    public void setForbidIds(long[] forbidIds) {
        this.forbidIds = forbidIds;
    }
}
