/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.proxy.JiPass;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbVerifier extends JbBase implements JiPass {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang(value = "验证主键", tag = "verifierId")
    @Id
    private String id;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getPassTime() {
        return passTime;
    }

    @Override
    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

}