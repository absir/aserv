/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-18 下午4:17:22
 */
package com.absir.aserv.system.bean.type;

import java.util.List;

/**
 * @author absir
 * 
 */
@SuppressWarnings("serial")
public class JtJsonList extends JtJsonValue {

	/**
	 * @throws ClassNotFoundException
	 */
	public JtJsonList() throws ClassNotFoundException {
		super(List.class.getName());
	}

}
