/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-6-14 下午4:08:18
 */
package com.absir.bean.config;

/**
 * @author absir
 */
public interface IBeanTypeFilter {

    /**
     * @param beanType
     * @return
     */
    public boolean filt(Class<?> beanType);
}
