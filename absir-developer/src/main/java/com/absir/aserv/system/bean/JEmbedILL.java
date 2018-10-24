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
public class JEmbedILL implements JiEmbed {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("编号")
    private Integer eid;

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("关联")
    private Long mid;

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("关联2")
    private Long mid2;

    public JEmbedILL() {
    }

    public JEmbedILL(Integer eid, Long mid, Long mid2) {
        this.eid = eid;
        this.mid = mid;
        this.mid2 = mid2;
    }

    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getMid2() {
        return mid2;
    }

    public void setMid2(Long mid2) {
        this.mid2 = mid2;
    }

    @Override
    public int hashCode() {
        return KernelObject.hashCode(eid) + KernelObject.hashCode(mid) + KernelObject.hashCode(mid2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof JEmbedILL) {
            JEmbedILL target = (JEmbedILL) obj;
            return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid) && KernelObject.equals(mid2, target.mid2);
        }

        return false;
    }

    @Override
    public String toString() {
        return eid + "`" + mid + "`" + mid2;
    }
}
