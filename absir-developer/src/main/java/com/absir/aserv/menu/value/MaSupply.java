/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月15日 下午1:01:38
 */
package com.absir.aserv.menu.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author absir
 *
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaSupply {

    /**
     * @return
     */
    String folder() default "内容管理";

    /**
     * @return
     */
    String name() default "列表";

    /**
     * @return
     */
    String method() default "list";

    /**
     * @return
     */
    String icon() default "";
}
