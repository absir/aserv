/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-12-20 下午12:34:44
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanDefine;

/**
 * @author absir
 */
public class BeanDefineWrappered extends BeanDefineWrapper {

    /**
     * @param beanDefine
     */
    public BeanDefineWrappered(BeanDefine beanDefine) {
        super(beanDefine);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.bean.core.BeanDefineWrapper#retrenchBeanDefine()
     *
     * 可去除嵌套定义
     */
    @Override
    public BeanDefine retrenchBeanDefine() {
        super.retrenchBeanDefine();
        return beanDefine;
    }
}
