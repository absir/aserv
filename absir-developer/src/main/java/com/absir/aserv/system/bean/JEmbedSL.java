/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-1 下午3:36:44
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiEmbed;
import com.absir.core.kernel.KernelObject;
import com.absir.validator.value.NotEmpty;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class JEmbedSL implements JiEmbed {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @NotEmpty
    @JaLang("编号")
    private String eid;

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("关联")
    private Long mid;

    public JEmbedSL() {

    }

    public JEmbedSL(String eid, Long mid) {
        this.eid = eid;
        this.mid = mid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(eid) + KernelObject.hashCode(mid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof JEmbedSL) {
            JEmbedSL target = (JEmbedSL) obj;
            return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid);
        }

        return false;
    }

    @Override
    public String toString() {
        return eid + "`" + mid;
    }

}
