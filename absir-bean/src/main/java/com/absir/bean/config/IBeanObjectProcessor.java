/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-14 下午4:11:43
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelList.Orderable;

public interface IBeanObjectProcessor extends Orderable {

    public void processBeanObject(BeanFactory beanFactory, BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy);

}
