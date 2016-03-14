/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-13 下午12:17:05
 */
package com.absir.bean.config;

import com.absir.bean.basis.BeanConfig;
import com.absir.core.kernel.KernelLang.CallbackTemplate;

import java.util.Map;
import java.util.Set;

/**
 * @author absir
 */
public interface IBeanConfigProvider {

    /**
     * @param beanConfig
     * @param propertyFilenames
     * @param loadedPropertyFilenames
     * @param beanConfigTemplates
     */
    public void loadBeanConfig(BeanConfig beanConfig, final Set<String> propertyFilenames, final Set<String> loadedPropertyFilenames, Map<String, CallbackTemplate<String>> beanConfigTemplates);

}
