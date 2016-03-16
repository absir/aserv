/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午5:30:07
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanDefineWrappered;

public class InjectBeanDefine extends BeanDefineWrappered {

    BeanScope beanScope;

    public InjectBeanDefine(BeanDefine beanDefine) {
        this(beanDefine, null);
    }

    public InjectBeanDefine(BeanDefine beanDefine, BeanScope beanScope) {
        super(beanDefine);
        this.beanScope = beanScope == null ? BeanScope.SINGLETON : beanScope;
    }

    @Override
    public BeanScope getBeanScope() {
        return beanScope;
    }
}
