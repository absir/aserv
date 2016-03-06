/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-15 上午10:51:47
 */
package com.absir.aserv.configure.conf;

import java.io.File;
import java.io.FileNotFoundException;

import com.absir.bean.core.BeanFactoryUtils;

/**
 * @author absir
 * 
 */
public class ConfigureBase {

	/**
	 * @return
	 * @throws FileNotFoundException
	 */
	protected File getConfigureFile() throws FileNotFoundException {
		return new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "conf/" + getClass().getSimpleName() + ".conf");
	}
}
