/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月15日 下午5:43:32
 */
package com.absir.aserv.crud.value;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

/**
 * @author absir
 */
public interface ICrudBean {

    /**
     * @param crud
     * @param handler
     */
    public void proccessCrud(Crud crud, CrudHandler handler);
}
