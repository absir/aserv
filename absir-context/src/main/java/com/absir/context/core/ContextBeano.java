/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-14 上午10:36:41
 */
package com.absir.context.core;

import java.io.Serializable;

public abstract class ContextBeano<ID extends Serializable> extends ContextBean<ID> {

    Class<?> keyClass;

    public Class<?> getKeyClass() {
        return keyClass == null ? getClass() : keyClass;
    }

}
