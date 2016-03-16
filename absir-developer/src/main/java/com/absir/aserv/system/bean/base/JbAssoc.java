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

@MappedSuperclass
public class JbAssoc extends JbBean implements JiAssoc {

    @JaLang("副本主键")
    private Long assocId;

    @Override
    public Long getAssocId() {
        return this.assocId;
    }

    public void setAssocId(Long assocId) {
        this.assocId = assocId;
    }
}
