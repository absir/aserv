/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月10日 下午1:17:43
 */
package com.absir.aserv.system.crud.value;

import com.absir.aserv.system.bean.JUpload;

import java.io.InputStream;

public interface IUploadProcessor {

    public InputStream process(String extension, JUpload upload, InputStream inputStream);

}
