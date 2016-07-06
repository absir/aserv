/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-28 下午6:52:41
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiEmbed;
import com.absir.core.kernel.KernelObject;
import com.absir.validator.value.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class JEmbedSS implements JiEmbed {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @NotEmpty
    @JaLang("编号")
    @Column(length = 100)
    private String eid;

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @NotEmpty
    @JaLang("关联")
    @Column(length = 230)
    private String mid;

    public JEmbedSS() {

    }

    public JEmbedSS(String eid, String mid) {
        this.eid = eid;
        this.mid = mid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(eid) + KernelObject.hashCode(mid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof JEmbedSS) {
            JEmbedSS target = (JEmbedSS) obj;
            return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid);
        }

        return false;
    }

    @Override
    public String toString() {
        return eid + "_" + mid;
    }
}
