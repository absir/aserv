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

/**
 * @author absir
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaEdit {

    /**
     * GROUP_SUG
     */
    public static final String GROUP_SUG = "sug";

    /**
     * GROUP_SUGGEST
     */
    public static final String GROUP_SUGGEST = "suggest";

    /**
     * GROUP_LIST
     */
    public static final String GROUP_LIST = "list";

    /**
     * GROUP_SEARCH
     */
    public static final String GROUP_SEARCH = "search";

    /**
     * EDIT_SUBTABLE
     */
    public static final String EDIT_SUBTABLE = "subtable";

    /**
     * 是否支持编辑
     *
     * @return
     */
    JeEditable editable() default JeEditable.ENABLE;

    /**
     * 字段所属组列表
     *
     * @return
     */
    String[] groups() default {};

    /**
     * 字段自定义类型
     *
     * @return
     */
    String[] types() default {};

    /**
     * 字段元信息扩展
     *
     * @return
     */
    String metas() default "";
}
