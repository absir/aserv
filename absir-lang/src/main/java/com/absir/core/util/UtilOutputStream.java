/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年12月29日 下午9:43:39
 */
package com.absir.core.util;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

public class UtilOutputStream extends OutputStream {

    DataOutput dataOutput;

    public UtilOutputStream(DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public void write(int b) throws IOException {
        dataOutput.write((byte) b);
    }
}
