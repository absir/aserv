/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-14 下午4:03:33
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanDefineProcessor extends Orderable {

    public BeanDefine getBeanDefine(BeanFactory beanFactory, BeanDefine beanDefine);

}
