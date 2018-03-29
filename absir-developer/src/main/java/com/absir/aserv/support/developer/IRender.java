/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月28日 下午2:49:49
 */
package com.absir.aserv.support.developer;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;

import java.io.IOException;
import java.io.OutputStream;

@Inject
public interface IRender {

    public static final IRender ME = BeanFactoryUtils.get(IRender.class);

    public String dev(int devTime);

    public String echo(String value);

    public String include(String path);

    public void include(String path, Object... renders) throws IOException;

    public String getPath(Object... renders) throws IOException;

    public String getFullPath(String path, Object... renders) throws IOException;

    public String getRealPath(String path, Object... renders) throws IOException;

    public void rend(OutputStream outputStream, String path, Object... renders) throws IOException;
}
