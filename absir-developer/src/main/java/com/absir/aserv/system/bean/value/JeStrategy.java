/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.value;

import com.absir.core.util.UtilEnum.EnumInteger;

public enum JeStrategy implements EnumInteger {

    /**
     * 默认策略
     */
    DEFAULT_STRATEGY(0X00),

    /**
     * 包涵策略
     */
    INCLUDE_STRATEGY(0X01),

    /**
     * 排除策略
     */
    EXCULDE_STRATEGY(0X02),;

    private int value;

    JeStrategy(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
