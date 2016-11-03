/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-18 下午1:34:38
 */
package com.absir.client.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rpc {

    // 名称
    String name() default "";

    // 超时
    int timeout() default 10000;

    boolean sendStream() default false;

    // 调用不考虑返回（不保证成功）
    boolean async() default false;

}
