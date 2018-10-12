/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月14日 下午4:50:14
 */
package com.absir.aserv.system.crud.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadRule {

    String value() default "";

    boolean thumb() default false;

    boolean tDel() default true;

    String tExt() default ".jpg";

    boolean tForceSize() default false;

    int tWidth() default 128;

    int tHeight() default 128;

    float tQuality() default 0.5f;

    int tScaleType() default 0;

}
