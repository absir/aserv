/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-18 下午4:17:22
 */
package com.absir.aserv.system.bean.type;

import java.util.List;

@SuppressWarnings("serial")
public class JtJsonList extends JtJsonValue {

    public JtJsonList() throws ClassNotFoundException {
        super(List.class.getName());
    }

}
