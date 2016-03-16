/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-14 上午9:40:11
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanSoftReference;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanSoftReferenceAware extends Orderable {

    public void registerSoftReference(BeanFactory beanFactory, BeanSoftReference beanSoftReference);

    public void unRegisterSoftReference(BeanFactory beanFactory, BeanSoftReference beanSoftReference);
}
