/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-1 下午3:36:28
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiEmbed;
import com.absir.core.kernel.KernelObject;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class JEmbedLI implements JiEmbed {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("编号")
    private Long eid;

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("关联")
    private Integer mid;

    public JEmbedLI() {
    }

    public JEmbedLI(Long eid, Integer mid) {
        this.eid = eid;
        this.mid = mid;
    }

    public Long getEid() {
        return eid;
    }

    public void setEid(Long eid) {
        this.eid = eid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(eid) + KernelObject.hashCode(mid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof JEmbedLI) {
            JEmbedLI target = (JEmbedLI) obj;
            return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid);
        }

        return false;
    }

    @Override
    public String toString() {
        return eid + "`" + mid;
    }
}
