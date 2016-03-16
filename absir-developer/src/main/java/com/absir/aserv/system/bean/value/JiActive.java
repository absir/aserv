/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-10 上午10:06:42
 */
package com.absir.aserv.system.bean.value;

import com.absir.aserv.system.bean.proxy.JiBase;
import com.absir.aserv.system.bean.proxy.JiPass;

public interface JiActive extends JiBase, JiPass {

    public long getBeginTime();

    public void setBeginTime(long beginTime);
}
