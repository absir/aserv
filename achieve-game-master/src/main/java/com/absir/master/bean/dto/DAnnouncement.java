/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月5日 下午8:18:38
 */
package com.absir.master.bean.dto;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

@Embeddable
@MappedSuperclass
public class DAnnouncement {

    @JaLang("标题")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    public String title;

    @JaLang("内容")
    @JaEdit(types = "text")
    @Column(length = 10240)
    public String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}
