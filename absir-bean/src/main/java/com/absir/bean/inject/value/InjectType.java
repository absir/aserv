/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-19 下午12:35:15
 */
package com.absir.bean.inject.value;

public enum InjectType {

    /**
     * 必须模式
     */
    Required,

    /**
     * 可选模式
     */
    Selectable,

    /**
     * 观察者模式
     */
    ObServed,

    /**
     * 实观察者模式(必须有值调用)
     */
    ObServeRealed,
}
