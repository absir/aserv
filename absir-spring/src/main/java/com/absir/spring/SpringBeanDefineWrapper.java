package com.absir.spring;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanDefineWrapper;

/**
 * Created by absir on 16/3/14.
 */
public class SpringBeanDefineWrapper extends BeanDefineWrapper {
    /**
     * @param beanDefine
     */
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
