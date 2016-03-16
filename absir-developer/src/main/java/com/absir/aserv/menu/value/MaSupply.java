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

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaSupply {

    String folder() default "内容管理";

    String name() default "列表";

    String method() default "list";

    String icon() default "";
}
