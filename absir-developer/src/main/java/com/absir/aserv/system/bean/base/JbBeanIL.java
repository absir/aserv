/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午2:38:37
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.JEmbedIL;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanIL extends JbBase {

    @JaLang("编号")
    @Id
    private JEmbedIL id;

    public JEmbedIL getId() {
        return id;
    }

    public void setId(JEmbedIL id) {
        this.id = id;
    }
}
