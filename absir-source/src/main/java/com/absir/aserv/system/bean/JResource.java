/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-25 上午10:35:58
 */
package com.absir.aserv.system.bean;

import com.absir.orm.value.JaEntity;
import com.absir.orm.value.JePermission;

import javax.persistence.Entity;

@JaEntity(permissions = JePermission.SELECT)
@Entity
public class JResource extends JbResource {

    private String fileMd5;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }
}
