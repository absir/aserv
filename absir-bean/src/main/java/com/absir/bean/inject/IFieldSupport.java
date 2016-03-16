/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-20 下午12:44:08
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

import java.lang.reflect.Field;

public interface IFieldSupport {

    public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Field field);
}
