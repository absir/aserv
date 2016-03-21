/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月15日 上午9:36:45
 */
package com.absir.aserv.system.crud.value;

import java.io.InputStream;

public interface IUploadRule {

    public String getUploadRuleName(String name, String extensionName);

    public InputStream proccessInputStream(String name, InputStream inputStream, String extensionName);
}
