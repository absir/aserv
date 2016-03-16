/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-29 下午1:45:55
 */
package com.absir.orm.value;

import org.hibernate.type.Type;

import java.util.Properties;

public interface JiType {

    public Type byClass(Class<?> typeClass, Properties parameters);
}
