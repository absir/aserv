/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-12 下午2:30:14
 */
package com.absir.aserv.menu.value;

public @interface MaMenu {

    String value() default "";

    int order() default 0;

    String ref() default "";

    String icon() default "";

}
