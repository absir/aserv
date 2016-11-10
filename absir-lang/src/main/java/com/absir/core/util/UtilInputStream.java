/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年12月29日 下午9:33:15
 */
package com.absir.core.util;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class UtilInputStream extends InputStream {

    DataInput dataInput;

    public UtilInputStream(DataInput dataInput) {
        this.dataInput = dataInput;
    }

    @Override
    public int read() throws IOException {
        try {
            return dataInput.readByte();

        } catch (EOFException e) {
            return -1;
        }
    }

}
