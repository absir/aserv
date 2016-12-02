/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年7月9日 下午8:42:08
 */
package com.absir.aserv.support.developer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DModel implements Serializable {

    public static final DModel DEFAULT = new DModel();

    private boolean filter;

    private boolean desc;

    private String value;

    private String queue;

    private String options;

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
