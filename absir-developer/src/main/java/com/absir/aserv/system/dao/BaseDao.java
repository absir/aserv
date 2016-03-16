/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 上午9:54:40
 */
package com.absir.aserv.system.dao;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

import java.io.Serializable;

public interface BaseDao<T, ID extends Serializable> {

    public void crud(Crud crud, CrudProperty property, CrudHandler crudHandler, T entity);

}
