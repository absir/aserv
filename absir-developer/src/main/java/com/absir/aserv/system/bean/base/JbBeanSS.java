/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午2:41:30
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanSS extends JbBase {

    @JaLang("编号")
    @Id
    private JEmbedSS id;

    public JEmbedSS getId() {
        return id;
    }

    public void setId(JEmbedSS id) {
        this.id = id;
    }

}
