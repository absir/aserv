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

import javax.persistence.Embeddable;

/**
 * @author absir
 */
@SuppressWarnings("serial")
@Embeddable
public class JEmbedSL implements JiEmbed {

    /**
     * eid
     */
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("编号")
    private String eid;

    /**
     * mid
     */
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("关联")
    private Long mid;

    /**
     *
     */
    public JEmbedSL() {

    }

    /**
     * @param eid
     * @param mid
     */
    public JEmbedSL(String eid, Long mid) {
        this.eid = eid;
        this.mid = mid;
    }

    /**
     * @return the eid
     */
    public String getEid() {
        return eid;
    }

    /**
     * @param eid the eid to set
     */
    public void setEid(String eid) {
        this.eid = eid;
    }

    /**
     * @return the mid
     */
    public Long getMid() {
        return mid;
    }

    /**
     * @param mid the mid to set
     */
    public void setMid(Long mid) {
        this.mid = mid;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return KernelObject.hashCode(eid) + KernelObject.hashCode(mid);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof JEmbedSL) {
            JEmbedSL target = (JEmbedSL) obj;
            return KernelObject.equals(eid, target.eid) && KernelObject.equals(mid, target.mid);
        }

        return false;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return eid + "_" + mid;
    }

}
