/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-24 上午10:04:02
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;

public interface ITypeSupport {

    public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Class<?> type);

}
