/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-12 下午2:35:42
 */
package com.absir.aserv.menu.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaEntity {

    boolean closed() default false;

    MaMenu[] parent() default {};

    String name() default "";

    MaMenu value() default @MaMenu;
}
