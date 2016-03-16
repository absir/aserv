/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-17 下午6:58:23
 */
package com.absir.aserv.game.value;

import com.absir.core.base.Base;

@SuppressWarnings("rawtypes")
public abstract class OBuff<T extends OObject> extends Base<Integer> {

    private Integer id;

    private String name;

    /**
     * 获取ID
     *
     * @return
     */
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * BUFF叠加
     *
     * @param buff
     * @return
     */
    public int against(OBuff buff) {
        return getClass() == buff.getClass() ? 1 : 0;
    }

    /**
     * BUFF生效
     *
     * @param self
     * @param result
     * @return
     */
    public abstract void effect(T self, IResult result);

    /**
     * BUFF持续
     *
     * @param self
     * @param time
     * @param result
     */
    public abstract void step(T self, long time, IResult result);

}
