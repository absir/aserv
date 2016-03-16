/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-15 上午10:51:47
 */
package com.absir.aserv.configure.conf;

import com.absir.bean.core.BeanFactoryUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class ConfigureBase {

    protected File getConfigureFile() throws FileNotFoundException {
        return new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "conf/" + getClass().getSimpleName() + ".conf");
    }
}
