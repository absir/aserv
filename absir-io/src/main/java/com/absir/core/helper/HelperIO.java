/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.core.helper;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

public class HelperIO extends IOUtils {

    public static void doWithReadLine(InputStream input, CallbackBreak<String> callback) throws IOException {
        doWithReadLine(input, Charset.defaultCharset(), callback);
    }

    public static void doWithReadLine(InputStream input, String encoding, CallbackBreak<String> callback)
            throws IOException {
        doWithReadLine(input, Charset.forName(encoding), callback);
    }

    public static void doWithReadLine(InputStream input, Charset encoding, CallbackBreak<String> callback)
            throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(input, encoding);
            doWithReadLine(reader, callback);

        } finally {
            if (reader != null) {
                reader.close();
            }

            input.close();
        }
    }

    public static void doWithReadLine(Reader reader, CallbackBreak<String> callback) throws IOException {
        if (reader instanceof BufferedReader) {
            doWithReadLine((BufferedReader) reader, callback);

        } else {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(reader);
                doWithReadLine(bufferedReader, callback);

            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
    }

    public static void doWithReadLine(BufferedReader reader, CallbackBreak<String> callback) throws IOException {
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                callback.doWith(line);

            } catch (BreakException e) {
                break;
            }
        }
    }
}
