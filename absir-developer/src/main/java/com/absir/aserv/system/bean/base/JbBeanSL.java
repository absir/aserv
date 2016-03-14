/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午2:40:38
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.JEmbedSL;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author absir
 */
@MappedSuperclass
public class JbBeanSL extends JbBase {

    @JaLang("编号")
    @Id
    private JEmbedSL id;

    /**
     * @return the id
     */
    public JEmbedSL getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(JEmbedSL id) {
        this.id = id;
    }
}
