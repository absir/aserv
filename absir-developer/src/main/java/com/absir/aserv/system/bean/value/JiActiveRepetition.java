/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月7日 下午3:11:03
 */
package com.absir.aserv.system.bean.value;

public interface JiActiveRepetition extends JiActive {

    public long getNextPassTime(long contextTime);

}
