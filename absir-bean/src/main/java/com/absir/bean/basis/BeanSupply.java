/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月11日 下午4:55:41
 */
package com.absir.bean.basis;

import java.util.Collection;

public interface BeanSupply {

    public Object getBeanObject(String beanName);

    public <T> T getBeanObject(Class<T> beanType);

    public <T> T getBeanObject(String beanName, Class<T> beanType);

    public <T> Collection<T> getBeanObjects(Class<T> beanType);

}
