/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 下午4:17:02
 */
package com.absir.aserv.system.bean.type;

import java.util.Map;

/**
 * @author absir
 *
 */
@SuppressWarnings("serial")
public class JtJsonMap extends JtJsonValue {

    /**
     * @throws ClassNotFoundException
     */
    public JtJsonMap() throws ClassNotFoundException {
        super(Map.class.getName());
    }

}
