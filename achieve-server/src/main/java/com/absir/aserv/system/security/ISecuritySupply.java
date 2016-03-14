/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-13 下午6:52:38
 */
package com.absir.aserv.system.security;

/**
 * @author absir
 *
 */
public interface ISecuritySupply {

    /**
     * @param securityContext
     */
    public void saveSession(SecurityContext securityContext);
}
