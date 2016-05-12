/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-25 下午7:20:14
 */
package com.absir.core.kernel;

import java.io.File;
import java.util.Comparator;

public class KernelUtil {

    public static final Comparator<String> VERSION_COMPARATOR = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return KernelUtil.compareVersion(o1, o2);
        }
    };

    public static int compare(byte[] from, byte[] to) {
        int len1 = from.length;
        int len2 = to.length;
        int compare = compareNo(from, to, len1, len2);
        if (compare == 0) {
            compare = len1 - len2;
        }

        return compare;
    }

    public static int compareNo(byte[] from, byte[] to) {
        return compareNo(from, to, from.length, to.length);
    }

    public static int compareNo(byte[] from, byte[] to, int len1, int len2) {
        int len = len1 < len2 ? len1 : len2;
        int compare;
        for (int i = 0; i < len; i++) {
            compare = from[i] - to[i];
            if (compare != 0) {
                return compare;
            }
        }

        return len1 >= len2 ? 0 : len1 - len2;
    }

    public static int compareNull(byte[] from, byte[] to) {
        if (from == null) {
            return to == null ? 0 : -1;
        }

        return to == null ? 1 : compare(from, to);
    }

    public static int compareEndNo(byte[] from, byte[] to) {
        return compareEndNo(from, to, from.length, to.length);
    }

    public static int compareEndNo(byte[] from, byte[] to, int len1, int len2) {
        int len = len1 < len2 ? len1 : len2;
        int compare;
        for (int i = 1; i <= len; i++) {
            compare = from[len1 - i] - to[len2 - i];
            if (compare != 0) {
                return compare;
            }
        }

        return len1 >= len2 ? 0 : len1 - len2;
    }

    public static int compareEndNoNull(byte[] from, byte[] to) {
        if (to == null) {
            return 0;
        }

        if (from == null) {
            return -1;
        }

        return compareEndNo(from, to);
    }

    public static int compareVersion(String version, String toVersion) {
        return compareVersion(version.getBytes(), toVersion.getBytes());
    }

    public static int compareVersion(byte[] version, byte[] toVersion) {
        int len1 = version.length;
        int len2 = toVersion.length;
        int len = len1 < len2 ? len1 : len2;
        byte v, t;
        int compare = 0;
        for (int i = 0; i < len; i++) {
            v = version[i];
            t = toVersion[i];
            if (compare == 0) {
                compare = v - t;
            }

            if (v == '.') {
                if (t == '.') {
                    if (compare != 0) {
                        return compare;
                    }

                } else {
                    return -9;
                }

            } else if (t == '.') {
                return 9;
            }
        }

        if (len1 == len2) {
            return compare;
        }

        if (len1 < len2) {
            if (toVersion[len1] == '.') {
                return compare == 0 ? len1 - len2 : compare;
            }

        } else {
            if (version[len2] == '.') {
                return compare == 0 ? len1 - len2 : compare;
            }
        }

        return len1 - len2;
    }

    public static File mustMatchFile(String root, String name, String... args) {
        if (!KernelString.isEmpty(name)) {
            root += '/' + name;
        }

        File rootFile = new File(root);
        if (rootFile.exists()) {
            if (rootFile.isDirectory()) {
                int length = args.length;
                File file = null;
                File nextFile = null;
                String arg;
                int fromIndex;
                for (int i = 0; i < length; i++) {
                    arg = args[i].toLowerCase();
                    nextFile = new File(root + '/' + arg);
                    fromIndex = arg.length();
                    while (!nextFile.exists()) {
                        fromIndex = arg.lastIndexOf('.', fromIndex - 1);
                        if (fromIndex <= 0) {
                            break;

                        } else {
                            nextFile = new File(root + '/' + arg.substring(0, fromIndex));
                        }
                    }

                    if (nextFile == null || !nextFile.exists()) {
                        break;

                    } else {
                        file = nextFile;
                        root = file.getPath();
                    }
                }

                if (file != null) {
                    return file;
                }
            }

        } else {
            rootFile = null;
        }

        return rootFile;
    }
}
