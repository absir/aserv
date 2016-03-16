/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-19 下午7:06:18
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeVote;

import javax.persistence.Embeddable;
import java.io.Serializable;

@SuppressWarnings("serial")
@Embeddable
public class JPermission implements Serializable {

    @JaLang("查看")
    private JeVote selectable;

    @JaLang("编辑")
    private JeVote updatable;

    @JaLang("创建")
    private JeVote insertable;

    @JaLang("删除")
    private JeVote deletable;

    @JaLang(value = "允许字段", tag = "allowFields")
    private String[] allows;

    @JaLang(value = "禁用字段", tag = "fobidFields")
    private String[] forbiddens;

    public JeVote getSelectable() {
        return selectable;
    }

    public void setSelectable(JeVote selectable) {
        this.selectable = selectable;
    }

    public JeVote getUpdatable() {
        return updatable;
    }

    public void setUpdatable(JeVote updatable) {
        this.updatable = updatable;
    }

    public JeVote getInsertable() {
        return insertable;
    }

    public void setInsertable(JeVote insertable) {
        this.insertable = insertable;
    }

    public JeVote getDeletable() {
        return deletable;
    }

    public void setDeletable(JeVote deletable) {
        this.deletable = deletable;
    }

    public String[] getAllows() {
        return allows;
    }

    public void setAllows(String[] allows) {
        this.allows = allows;
    }

    public String[] getForbiddens() {
        return forbiddens;
    }

    public void setForbiddens(String[] forbiddens) {
        this.forbiddens = forbiddens;
    }
}
