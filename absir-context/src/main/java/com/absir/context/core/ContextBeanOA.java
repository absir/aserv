/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-14 上午10:36:41
 */
package com.absir.context.core;

import java.io.Serializable;

public abstract class ContextBeanOA<ID extends Serializable> extends ContextBean<ID> {

    Class<?> contextClass;

    public Class<?> getContextClass() {
        return contextClass == null ? getClass() : contextClass;
    }

}
