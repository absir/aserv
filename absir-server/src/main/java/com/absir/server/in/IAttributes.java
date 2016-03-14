/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-3-14 上午10:57:01
 */
package com.absir.server.in;

/**
 * @author absir
 */
public interface IAttributes {

    /**
     * @param name
     * @return
     */
    public Object getAttribute(String name);

    /**
     * @param name
     * @param obj
     * @return
     */
    public void setAttribute(String name, Object obj);

}
