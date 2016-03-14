/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.bean.proxy;

import com.absir.aserv.system.bean.base.JbRecycleBase;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;

import javax.persistence.Entity;

/**
 * @author absir
 *
 */
@JaCrud(value = "logCrudFactory", cruds = Crud.DELETE, parameters = JpRecycleBase.RECYCLE)
public interface JpRecycleBase {

    /**
     * RECYCLE
     */
    public static final String RECYCLE = "Recycle";

    /**
     * @author absir
     */
    @Entity(name = RECYCLE)
    class Recycle extends JbRecycleBase {

    }
}
