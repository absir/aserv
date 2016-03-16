/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月19日 上午10:16:12
 */
package com.absir.aserv.system.bean.value;

import com.absir.aserv.system.bean.proxy.JiBase;

public interface JiOpenValue<T> extends JiBase {

    public boolean isOpen();

    public T getValue();

}
