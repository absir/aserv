/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-10 上午11:28:08
 */
package com.absir.aserv.crud;

import com.absir.aserv.support.developer.JCrudField;
import com.absir.orm.value.JoEntity;

/**
 * @author absir
 */
public interface ICrudFactory {

    /**
     * @param joEntity
     * @param crudField
     * @return
     */
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField);

}
