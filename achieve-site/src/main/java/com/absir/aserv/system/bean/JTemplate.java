/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月29日 下午3:24:06
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author absir
 *
 */
@Entity
public class JTemplate extends JbBase {

    /**
     * id
     */
    @JaLang(value = "模版名称", tag = "templateName")
    @Id
    private String id;

    /**
     * content
     */
    @JaLang("内容")
    @JaEdit(types = "html")
    @Lob
    private String content;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
