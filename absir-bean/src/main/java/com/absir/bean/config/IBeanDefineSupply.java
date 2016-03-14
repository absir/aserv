/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-16 下午10:58:00
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.core.kernel.KernelList.Orderable;

import java.util.List;

/**
 * @author absir
 */
public interface IBeanDefineSupply extends Orderable {

    /**
     * @param beanFactory
     * @param beanType
     * @return
     */
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType);

}
