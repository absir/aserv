/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-22 下午4:50:38
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.menu.IMenuBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.menu.value.MeUrlType;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.proxy.JiTree;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

@MaEntity(parent = {@MaMenu("菜单管理")}, name = "菜单")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JMenu extends JbBean implements IMenuBean, JiTree<JMenu> {

    @JaLang(value = "父级菜单", tag = "parentMenu")
    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    @ManyToOne
    private JMenu parent;

    @JaLang(value = "菜单名称", tag = "menuName")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST}, listColType = 1)
    private String name;

    @JaLang("类型")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String type;

    @JaLang("排序")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int ordinal;

    @JaLang("链接")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String url;

    @JaLang("标注")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String ref;

    @JaLang("链接类型")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private MeUrlType urlType;

    @JaLang("图标")
    @JaEdit(types = "icon", metas = "{\"type\":\"menu\"}", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String icon;

    @JaLang(value = "子级菜单", tag = "subMenu")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "parent")
    @OrderBy("ordinal")
    private List<JMenu> children;

    public JMenu getParent() {
        return parent;
    }

    public void setParent(JMenu parent) {
        this.parent = parent;
    }

    @Langs
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public MeUrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(MeUrlType urlType) {
        this.urlType = urlType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<JMenu> getChildren() {
        return children;
    }

    public void setChildren(List<JMenu> children) {
        this.children = children;
    }

    @Override
    public int getOrder() {
        return ordinal;
    }
}
