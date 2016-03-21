/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-24 下午1:02:16
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;

public interface ICrudProcessorInput<T> extends ICrudProcessor {

    public boolean isMultipart();

    public T crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input);

    public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, T inputBody);

}
