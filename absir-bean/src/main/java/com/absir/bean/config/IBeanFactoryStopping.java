/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-9 下午4:00:53
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanFactoryStopping extends Orderable {

    public void stopping(BeanFactory beanFactory);

}
