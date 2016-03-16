/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-20 下午1:27:18
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

public class BeanDefineMerged extends BeanDefineWrappered {

    String beanName;

    BeanScope beanScope;

    private Object beanComponent;

    public BeanDefineMerged(BeanDefine beanDefine, String beanName, BeanScope beanScope, Object beanComponent) {
        super(beanDefine);
        this.beanName = beanName;
        this.beanScope = beanScope;
        this.beanComponent = beanComponent;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public BeanScope getBeanScope() {
        return beanScope;
    }

    @Override
    public Object getBeanComponent() {
        return beanComponent;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        return BeanDefineAbstractor.getBeanObject(beanFactory, beanDefine instanceof BeanDefineWrapper ? ((BeanDefineWrapper) beanDefine).retrenchBeanDefine() : beanDefine, beanDefineRoot, this);
    }
}
