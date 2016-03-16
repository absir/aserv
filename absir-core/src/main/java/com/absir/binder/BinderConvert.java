/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午2:07:22
 */
package com.absir.binder;

import com.absir.core.dyna.DynaConvert;
import com.absir.core.kernel.KernelLang.BreakException;

import java.lang.reflect.Type;

public interface BinderConvert extends DynaConvert {

    public Object to(Object obj, String name, Type toType, BreakException breakException) throws Exception;

}
