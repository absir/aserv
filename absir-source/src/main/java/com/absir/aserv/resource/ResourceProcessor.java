/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-25 上午10:34:19
 */
package com.absir.aserv.resource;

import java.io.File;

public interface ResourceProcessor {

    public boolean supports(ResourceScanner scanner);

    public boolean supportsDirectory(File directoryFile, ResourceScanner scanner);

    public void doProcessor(File resourceFile, ResourceScanner scanner);

}
