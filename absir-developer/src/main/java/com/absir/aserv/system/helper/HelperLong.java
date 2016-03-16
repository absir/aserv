/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.system.helper;

import com.absir.core.kernel.KernelDyna;

public class HelperLong {

    /**
     * IP地址转换数字
     *
     * @param iPaddress
     * @param iPcount
     * @return
     */
    public static long longIP(String iPaddress, int iPcount) {
        return longString(iPaddress, '.', iPcount);
    }

    /**
     * 规则间隔转换数字
     *
     * @param string
     * @param glues
     * @param count
     * @return
     */
    public static long longString(String string, char glues, int count) {
        long lg = 0;
        int from;
        int last = string.length();
        for (int i = 0; count < 0 || (count != 0 && --count >= 0); i += 8) {
            from = HelperString.lastIndexOf(string, glues, last - 1);
            if (from <= 0) {
                count = 0;
            }

            lg += ((long) KernelDyna.toLong(string.substring(from + 1, last), 0L)) << i;
            last = from;
        }

        return lg;
    }

    /**
     * IPV4转换数字
     *
     * @param iPAddress
     * @return
     */
    public static long longIPV4(String iPAddress) {
        return longIP(iPAddress, 4);
    }

    public static String stringLong(long lg, char glues, int count) {
        int i = count * 8;
        StringBuilder sb = new StringBuilder();
        sb.append((int) ((lg >> (i -= 8)) & 0XFF));
        while (true) {
            if (i <= 0) {
                break;
            }

            sb.append(glues);
            sb.append((int) ((lg >> (i -= 8)) & 0XFF));
        }

        return sb.toString();
    }

    /**
     * 数字转换IPV4
     *
     * @param longIP
     * @return
     */
    public static String longIPV4(long longIP) {
        return stringLong(longIP, '.', 4);
    }
}
