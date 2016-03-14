/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014年10月11日 下午4:55:41
 */
package com.absir.bean.basis;

import java.util.Collection;

/**
 * @author absir
 */
public interface BeanSupply {

    /**
     * @param beanName
     * @return
     */
    public Object getBeanObject(String beanName);

    /**
     * @param beanType
     * @return
     */
    public <T> T getBeanObject(Class<T> beanType);

    /**
     * @param beanName
     * @param beanType
     * @return
     */
    public <T> T getBeanObject(String beanName, Class<T> beanType);

    /**
     * @param beanType
     * @return
     */
    public <T> Collection<T> getBeanObjects(Class<T> beanType);

}
