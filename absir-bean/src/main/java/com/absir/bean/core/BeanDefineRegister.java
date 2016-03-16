/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年12月17日 上午10:00:00
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;

public class BeanDefineRegister extends BeanDefineWrapper {

    public BeanDefineRegister(BeanDefine beanDefine) {
        super(beanDefine);
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return beanDefine.getBeanObject(beanFactory);
    }

}
