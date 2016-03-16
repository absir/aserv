/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午6:05:46
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;

public class BeanDefinePrototype extends BeanDefineWrapper {

    public BeanDefinePrototype(BeanDefine beanDefine) {
        super(beanDefine);
    }

    @Override
    public BeanScope getBeanScope() {
        return BeanScope.PROTOTYPE;
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return beanDefine.getBeanObject(beanFactory);
    }
}
