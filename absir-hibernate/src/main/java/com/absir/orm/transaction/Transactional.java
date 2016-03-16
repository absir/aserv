package com.absir.orm.transaction;

import java.lang.annotation.*;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Transactional {

    TxType value() default TxType.REQUIRED;

    Class<?>[] rollbackOn() default {};

    Class<?>[] dontRollbackOn() default {};

    public enum TxType {

        REQUIRED,

        REQUIRES_NEW,

        MANDATORY,

        SUPPORTS,

        NOT_SUPPORTED,

        NEVER
    }

}
