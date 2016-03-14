/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月29日 下午3:37:43
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbVerifier;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;

/**
 * @author absir
 *
 */
@Entity
public class JVerifier extends JbVerifier {

    /**
     * tag
     */
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang("标签")
    private String tag;

    /**
     * value
     */
    @JaLang("内容")
    private String value;

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
