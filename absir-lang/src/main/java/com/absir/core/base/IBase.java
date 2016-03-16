/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-14 下午3:15:31
 */
package com.absir.core.base;

import java.io.Serializable;

public interface IBase<ID extends Serializable> {

    public ID getId();

}
