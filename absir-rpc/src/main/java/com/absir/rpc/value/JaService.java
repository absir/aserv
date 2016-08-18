package com.absir.rpc.value;

/**
 * Created by absir on 16/8/18.
 */
public @interface JaService {

    // 代理对象
    Class<?> interfaceClass() default void.class;

    // 版本
    int version() default 1;
}
