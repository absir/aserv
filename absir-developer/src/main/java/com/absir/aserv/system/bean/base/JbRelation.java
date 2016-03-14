/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbRelation extends JbAssoc {

    @JaLang("关联主键")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long relateId;

    /**
     * @return the relateId
     */
    public long getRelateId() {
        return relateId;
    }

    /**
     * @param relateId the relateId to set
     */
    public void setRelateId(long relateId) {
        this.relateId = relateId;
    }
}
