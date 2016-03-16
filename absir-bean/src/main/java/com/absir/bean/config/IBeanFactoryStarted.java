/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-19 下午2:03:34
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanFactoryStarted extends Orderable {

    public void started(BeanFactory beanFactory);
}
