/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-29 上午11:02:30
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class XlsBaseUpdate extends XlsBase implements JiUpdate {

    private transient long updateTime;

    @JsonIgnore
    public long getUpdateTime() {
        return updateTime;
    }
}
