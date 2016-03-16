/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:24:20
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbBeanL extends JbBase {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
