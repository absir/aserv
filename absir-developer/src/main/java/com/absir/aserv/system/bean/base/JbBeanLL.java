/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-20 下午2:38:37
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.JEmbedLL;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanLL extends JbBase {

    @JaLang("编号")
    @Id
    private JEmbedLL id;

    /**
     * @return the id
     */
    public JEmbedLL getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(JEmbedLL id) {
        this.id = id;
    }
}
