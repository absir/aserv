/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-12 下午4:50:55
 */
package com.absir.aserv.system.bean.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaEdit {

    public static final String GROUP_SUG = "sug";

    public static final String GROUP_SUGGEST = "suggest";

    public static final String GROUP_LIST = "list";

    public static final String GROUP_SEARCH = "search";

    public static final String EDIT_SUBTABLE = "subtable";

    /**
     * 是否支持编辑
     */
    JeEditable editable() default JeEditable.ENABLE;

    /**
     * 字段所属组列表
     */
    String[] groups() default {};

    /**
     * 字段自定义类型
     */
    String[] types() default {};

    /**
     * 字段元信息扩展
     */
    String metas() default "";

    /*
     * 列表显示类型
     */
    int listColType() default 0;

    /*
     * suggest授权
     */
    boolean suggest() default false;
}
