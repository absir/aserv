/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-7 下午4:57:40
 */
package com.absir.aserv.system.bean.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaCrud {

    /**
     * 处理工厂引用名
     *
     * @return
     */
    String value() default "";

    /**
     * 处理工厂引用类
     *
     * @return
     */
    Class<?> factory() default void.class;

    /**
     * 工厂构造参数
     *
     * @return
     */
    String[] parameters() default {};

    /**
     * 关联处理
     *
     * @return
     */
    JaCrud.Crud[] cruds() default {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE, JaCrud.Crud.DELETE};

    public static enum Crud {

        /**
         * 创建处理 CREATE
         */
        CREATE,

        /**
         * 更新处理 UPDATE
         */
        UPDATE,

        /**
         * 删除处理 DELETE
         */
        DELETE,

        /**
         * 全部处理|LIST COMPLETE
         */
        COMPLETE;
    }
}
