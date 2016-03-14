/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-4-23 下午1:29:26
 */
package com.absir.client.helper;

import com.absir.core.base.Environment;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelCharset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
public class HelperClient {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperClient.class);

    /**
     * @param urlConnection
     * @return
     * @throws IOException
     */
    public static InputStream openConnection(HttpURLConnection urlConnection) throws IOException {
        InputStream inputStream = urlConnection.getResponseCode() >= 400 ? null : urlConnection.getInputStream();
        return inputStream == null ? urlConnection.getErrorStream() : inputStream;
    }

    /**
     * @param urlConnection
     * @param type
     * @return
     * @throws IOException
     */
    public static <T> T openConnection(HttpURLConnection urlConnection, Class<T> type) throws IOException {
        if (type == null || type == InputStream.class) {
            return (T) openConnection(urlConnection);

        } else if (type == String.class) {
            return (T) HelperIO.toString(openConnection(urlConnection), KernelCharset.getDefault());

        } else {
            return openConnectionJson(urlConnection, type);
        }
    }

    /**
     * @param urlConnection
     * @return
     * @throws IOException
     */
    public static <T> T openConnectionJson(HttpURLConnection urlConnection, Class<T> type) throws IOException {
        return HelperJson.OBJECT_MAPPER.reader(type).readValue(openConnection(urlConnection));
    }

    /**
     * @param url
     * @param post
     * @param postParameters
     * @param type
     * @return
     * @throws IOException
     */
    public static <T> T openConnection(String url, Map<String, String> postParameters, Class<T> type) {
        byte[] postBytes = null;
        if (postParameters != null) {
            StringBuffer paramsBuffer = new StringBuffer();
            for (Entry<String, String> entry : postParameters.entrySet()) {
                if (paramsBuffer.length() > 0) {
                    paramsBuffer.append("&");
                }

                paramsBuffer.append(entry.getKey()).append("=").append(entry.getValue());
            }

            postBytes = paramsBuffer.toString().getBytes();
        }

        return openConnection(url, postBytes == null ? false : true, postBytes, 0, type);
    }

    /**
     * @param url
     * @param post
     * @param postBytes
     * @param type
     * @return
     */
    public static <T> T openConnection(String url, boolean post, byte[] postBytes, Class<T> type) {
        return openConnection(url, post, postBytes, 0, type);
    }

    /**
     * @param url
     * @param post
     * @param postBytes
     * @param off
     * @param type
     * @return
     */
    public static <T> T openConnection(String url, boolean post, byte[] postBytes, int off, Class<T> type) {
        return openConnection(url, post, postBytes, off, 0, type);
    }

    /**
     * @param url
     * @param post
     * @param postBytes
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String url, boolean post, byte[] postBytes, int off, int len)
            throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
        if (post || postBytes != null) {
            urlConnection.setRequestMethod("POST");
            if (postBytes != null) {
                if (len <= 0) {
                    len = postBytes.length;
                }

                if (len > 0) {
                    // 必需的
                    urlConnection.addRequestProperty("Content-type", "text/plain");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    urlConnection.getOutputStream().write(postBytes, off, len);
                }
            }
        }

        return urlConnection;
    }

    /**
     * @param url
     * @param post
     * @param postBytes
     * @param offset
     * @param length
     * @param type
     * @return
     */
    public static <T> T openConnection(String url, boolean post, byte[] postBytes, int off, int len, Class<T> type) {
        try {
            HttpURLConnection urlConnection = openConnection(url, post, postBytes, off, len);
            return openConnection(urlConnection, type);

        } catch (Throwable e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }

            LOGGER.error("", e);
        }

        return null;
    }

    /**
     * @param url
     * @param post
     * @param postBytes
     * @param off
     * @param len
     * @param type
     * @return
     */
    public static <T> DResponse<T> requestConnection(String url, boolean post, byte[] postBytes, int off, int len,
                                                     Class<T> type) {
        DResponse<T> response = null;
        try {
            HttpURLConnection urlConnection = openConnection(url, post, postBytes, off, len);
            response = new DResponse<T>();
            response.code = urlConnection.getResponseCode();
            response.input = openConnection(urlConnection);
            if (response.code == 200) {
                response.value = openConnection(urlConnection, type);
            }

        } catch (Throwable e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }

            LOGGER.error("", e);
        }

        return response;
    }

    /**
     * @param <T>
     * @author absir
     */
    public static class DResponse<T> {

        /**
         * code
         */
        public int code;

        /**
         * inputStream
         */
        public InputStream input;

        /**
         * value
         */
        public T value;

    }
}
