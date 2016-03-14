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

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author absir
 */
@MaEntity(parent = {@MaMenu("菜单管理")}, name = "权限")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JMenuPermission extends JbBase {

    @JaLang(value = "标识", tag = "identifie")
    @Id
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String id;

    @JaLang("标题")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String caption;

    @JaLang(value = "授权角色", tag = "allowUserRoles")
    @JaName(value = "JUser")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long allowIds[];

    @JaLang(value = "禁用角色", tag = "forbidUserRoles")
    @JaName(value = "JUser")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long forbidIds[];

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the caption
     */
    @Langs
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return the allowIds
     */
    public long[] getAllowIds() {
        return allowIds;
    }

    /**
     * @param allowIds the allowIds to set
     */
    public void setAllowIds(long[] allowIds) {
        this.allowIds = allowIds;
    }

    /**
     * @return the forbidIds
     */
    public long[] getForbidIds() {
        return forbidIds;
    }

    /**
     * @param forbidIds the forbidIds to set
     */
    public void setForbidIds(long[] forbidIds) {
        this.forbidIds = forbidIds;
    }
}
