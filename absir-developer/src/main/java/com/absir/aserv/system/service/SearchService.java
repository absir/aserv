/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-23 下午1:32:26
 */
package com.absir.aserv.system.service;

import com.absir.orm.transaction.value.Transaction;

import java.io.Serializable;
import java.util.Set;

/**
 * @author absir
 *
 */
@Transaction(readOnly = true)
public interface SearchService {

    /**
     * @param entityName
     * @param ids
     * @return
     */
    public Set<Object> getSearch(String entityName, Object... ids);

    /**
     * @param entityName
     * @param ids
     * @return
     */
    public Set<Serializable> getSearchIds(String entityName, Object... ids);
}
