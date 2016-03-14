/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年4月9日 下午7:36:21
 */
package com.absir.slave.resolver;

import com.absir.client.SocketAdapter.CallbackAdapte;
import com.absir.core.kernel.KernelList.Orderable;

/**
 * @author absir
 */
public interface ISlaveCallback extends CallbackAdapte, Orderable {

    /**
     * @return
     */
    public int getCallbackIndex();

}
