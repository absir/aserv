/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午2:38:37
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.JEmbedLS;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanLS extends JbBase {

    @JaLang("编号")
    @Id
    private JEmbedLS id;

    /**
     * @return the id
     */
    public JEmbedLS getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(JEmbedLS id) {
        this.id = id;
    }
}
