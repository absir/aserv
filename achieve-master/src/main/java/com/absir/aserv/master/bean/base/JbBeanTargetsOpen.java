/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月14日 上午10:28:04
 */
package com.absir.aserv.master.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.MappedSuperclass;

/**
 * @author absir
 *
 */
@MappedSuperclass
public class JbBeanTargetsOpen extends JbBeanTargets {

    @JaLang("开启间隔天数")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openSubDay;

    @JaLang("开启持续天数")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int openLifeDay;

    /**
     * @return the openSubDay
     */
    public int getOpenSubDay() {
        return openSubDay;
    }

    /**
     * @param openSubDay the openSubDay to set
     */
    public void setOpenSubDay(int openSubDay) {
        this.openSubDay = openSubDay;
    }

    /**
     * @return the openLifeDay
     */
    public int getOpenLifeDay() {
        return openLifeDay;
    }

    /**
     * @param openLifeDay
     *            the openLifeDay to set
     */
    public void setOpenLifeDay(int openLifeDay) {
        this.openLifeDay = openLifeDay;
    }
}
