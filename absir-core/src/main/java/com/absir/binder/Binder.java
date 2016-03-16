/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-8 下午12:46:06
 */
package com.absir.binder;

import java.lang.reflect.Type;

public interface Binder {

    public Object to(Object obj, String name, Type toType);

    public Object to(Object obj, String name, Class<?> toClass);
}
