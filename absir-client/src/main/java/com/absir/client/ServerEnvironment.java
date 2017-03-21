package com.absir.client;

/**
 * Created by absir on 2017/3/21.
 */
public class ServerEnvironment {

    private static long startTime = System.currentTimeMillis();

    public static long getStartTime() {
        return startTime;
    }

    public static void reloadServer() {
        startTime = System.currentTimeMillis();
    }
}
