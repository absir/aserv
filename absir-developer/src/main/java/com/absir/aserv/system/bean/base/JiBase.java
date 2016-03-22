package com.absir.aserv.system.bean.base;

import java.io.Serializable;

/**
 * Created by absir on 16/3/22.
 */
public interface JiBase<T extends Serializable> {

    public abstract T getId();

    public abstract void setId(T id);
}
