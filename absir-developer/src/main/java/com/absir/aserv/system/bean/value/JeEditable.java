/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-18 下午7:56:01
 */
package com.absir.aserv.system.bean.value;

public enum JeEditable {

    /**
     * 可编辑
     */
    ENABLE,

    /**
     * 不显示(不安全,可以透传;可以用lockable或者group屏蔽)
     */
    DISABLE,

    /**
     * 锁定的(不安全)
     */
    LOCKED,

    /**
     * 可配置的
     */
    OPTIONAL,

    /**
     * 锁定显示(安全)
     */
    LOCKABLE,

    /**
     * 锁定不显示(安全)
     */
    LOCKNONE,
}
