/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-17 下午8:18:24
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.basis.BeanScope;
import com.absir.core.kernel.KernelLang.CloneTemplate;

/**
 * @author absir
 */
public class BeanDefineCloneableType extends BeanDefineAbstract {

    /**
     * beanType
     */
    Class<?> beanType;

    /**
     * beanObject
     */
    CloneTemplate<?> beanObject;

    /**
     * @param beanType
     * @param beanObject
     */
    public BeanDefineCloneableType(Class<?> beanType, CloneTemplate<?> beanObject) {
        this(beanType, null, beanObject);
    }

    /**
     * @param beanType
     * @param beanName
     * @param beanObject
     */
    public BeanDefineCloneableType(Class<?> beanType, String beanName, CloneTemplate<?> beanObject) {
        this.beanType = beanType;
        this.beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
        this.beanObject = beanObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanType()
     */
    @Override
    public Class<?> getBeanType() {
        return beanType;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanScope()
     */
    @Override
    public BeanScope getBeanScope() {
        return BeanScope.PROTOTYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.android.bean.value.IBeanDefine#getBeanComponent()
     */
    @Override
    public Object getBeanComponent() {
        return beanObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.basis.BeanDefine#getBeanObject(com.absir.bean.basis.
     * BeanFactory)
     */
    @Override
    public Object getBeanObject(BeanFactory beanFactory) {
        return beanObject.clone();
    }
}
