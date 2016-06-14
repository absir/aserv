/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-4 下午5:18:33
 */
package com.absir.aserv.game.value;

import java.lang.reflect.TypeVariable;

public interface IBuffFrom<T> {

    public static final TypeVariable T_VARIABLE = IBuffFrom.class.getTypeParameters()[0];

    public boolean supportsFrom(Object from);

}
