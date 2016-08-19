package com.absir.rpc.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by absir on 16/8/18.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaService {

    // 代理对象
    Class<?> interfaceClass() default void.class;

    // 版本
    int version() default 1;

    // 超时
    long timeout() default 30;
}
