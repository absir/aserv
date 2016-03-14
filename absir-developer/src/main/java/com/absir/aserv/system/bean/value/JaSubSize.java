/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-16 上午11:35:12
 */
package com.absir.aserv.system.bean.value;

import com.absir.aserv.system.crud.SubSizeCrudFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 */
@JaCrud(factory = SubSizeCrudFactory.class, parameters = {""})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaSubSize {

    /**
     * @return
     */
    String value();
}
