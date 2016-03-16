package com.absir.spring;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineWrapper;

public class SpringBeanDefineWrapper extends BeanDefineWrapper {

    public SpringBeanDefineWrapper(BeanDefine beanDefine) {
        super(beanDefine);
    }

    @Override
    public Object getBeanObject(BeanFactory beanFactory, BeanDefine beanDefineRoot, BeanDefine beanDefineWrapper) {
        Object beanObject = super.getBeanObject(beanFactory, beanDefineRoot, beanDefineWrapper);
        SpringBeanDefineAware.getContext().getAutowireCapableBeanFactory().autowireBean(beanObject);
        return beanObject;
    }
}
