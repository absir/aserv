/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.base;

import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JbRecycleBase extends JbBase implements JiUpdate {

    @Id
    @JaLang("纪录编号")
    private Long id;

    @JaLang("修改时间")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE, Crud.UPDATE})
    private long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
