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

/**
 * @author absir
 *
 */
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

    /**
     * @return the selectable
     */
    public JeVote getSelectable() {
        return selectable;
    }

    /**
     * @param selectable the selectable to set
     */
    public void setSelectable(JeVote selectable) {
        this.selectable = selectable;
    }

    /**
     * @return the updatable
     */
    public JeVote getUpdatable() {
        return updatable;
    }

    /**
     * @param updatable
     *            the updatable to set
     */
    public void setUpdatable(JeVote updatable) {
        this.updatable = updatable;
    }

    /**
     * @return the insertable
     */
    public JeVote getInsertable() {
        return insertable;
    }

    /**
     * @param insertable
     *            the insertable to set
     */
    public void setInsertable(JeVote insertable) {
        this.insertable = insertable;
    }

    /**
     * @return the deletable
     */
    public JeVote getDeletable() {
        return deletable;
    }

    /**
     * @param deletable
     *            the deletable to set
     */
    public void setDeletable(JeVote deletable) {
        this.deletable = deletable;
    }

    /**
     * @return the allows
     */
    public String[] getAllows() {
        return allows;
    }

    /**
     * @param allows
     *            the allows to set
     */
    public void setAllows(String[] allows) {
        this.allows = allows;
    }

    /**
     * @return the forbiddens
     */
    public String[] getForbiddens() {
        return forbiddens;
    }

    /**
     * @param forbiddens
     *            the forbiddens to set
     */
    public void setForbiddens(String[] forbiddens) {
        this.forbiddens = forbiddens;
    }
}
