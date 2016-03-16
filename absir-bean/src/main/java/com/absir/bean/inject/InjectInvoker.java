/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-19 下午2:42:59
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

public abstract class InjectInvoker implements Orderable {

    @Override
    public int getOrder() {
        return 0;
    }

    public abstract void invoke(BeanFactory beanFactory, Object beanObject);
}
