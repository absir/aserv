/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-15 上午10:42:21
 */
package com.absir.context.bean;

import com.absir.core.base.IBase;

import java.io.Serializable;

public interface IContextBean<ID extends Serializable> extends IBase<ID>, IContext {

}
