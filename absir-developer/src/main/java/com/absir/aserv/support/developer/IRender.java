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

/**
 * @author absir
 *
 */
@Inject
public interface IRender {

    /**
     * ME
     */
    public static final IRender ME = BeanFactoryUtils.get(IRender.class);

    /**
     * @param value
     * @return
     */
    public String echo(String value);

    /**
     * @param path
     * @return
     */
    public String include(String path);

    /**
     * @param path
     * @param renders
     * @throws IOException
     */
    public void include(String path, Object... renders) throws IOException;

    /**
     * @param renders
     * @return
     * @throws IOException
     */
    public String getPath(Object... renders) throws IOException;

    /**
     * @param path
     * @param renders
     * @return
     * @throws IOException
     */
    public String getFullPath(String path, Object... renders) throws IOException;

    /**
     * @param path
     * @param renders
     * @return
     * @throws IOException
     */
    public String getRealPath(String path, Object... renders) throws IOException;

    /**
     * @param outputStream
     * @param path
     * @param renders
     * @throws IOException
     */
    public void rend(OutputStream outputStream, String path, Object... renders) throws IOException;
}
