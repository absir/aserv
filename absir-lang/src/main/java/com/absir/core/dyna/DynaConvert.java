/**
 * Copyright 2013 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.core.dyna;

import com.absir.core.kernel.KernelLang.BreakException;

import java.util.Map;

/**
 * @author absir
 */
public interface DynaConvert {

    /**
     * @param obj
     * @param name
     * @param toClass
     * @param breakException
     * @return
     * @throws Exception
     */
    public Object to(Object obj, String name, Class<?> toClass, BreakException breakException) throws Exception;

    /**
     * @param map
     * @param name
     * @param toClass
     * @param breakException
     * @return
     * @throws Exception
     */
    public Object mapTo(Map<?, ?> map, String name, Class<?> toClass, BreakException breakException) throws Exception;
}
