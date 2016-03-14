/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JiAssoc;

import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbAssoc extends JbBean implements JiAssoc {

    /**
     * assocId
     */
    @JaLang("副本主键")
    private Long assocId;

    /*
     * (non-Javadoc)
     *
     * @see com.absir.aserv.support.entity.value.JiAssoc#getAssocId()
     */
    @Override
    public Long getAssocId() {
        return this.assocId;
    }

    /**
     * @param assocId
     */
    public void setAssocId(Long assocId) {
        this.assocId = assocId;
    }
}
