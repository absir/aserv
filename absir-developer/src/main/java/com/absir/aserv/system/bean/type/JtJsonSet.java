/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 下午4:16:53
 */
package com.absir.aserv.system.bean.type;

import java.util.Set;

/**
 * @author absir
 *
 */
@SuppressWarnings("serial")
public class JtJsonSet extends JtJsonValue {

    /**
     * @throws ClassNotFoundException
     */
    public JtJsonSet() throws ClassNotFoundException {
        super(Set.class.getName());
    }

}
