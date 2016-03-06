/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-15 上午10:42:21
 */
package com.absir.context.bean;

import java.io.Serializable;

import com.absir.core.base.IBase;

/**
 * @author absir
 * 
 */
public interface IContextBean<ID extends Serializable> extends IBase<ID>, IContext {

}
