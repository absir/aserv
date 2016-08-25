/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-10 下午1:50:51
 */
package com.absir.core.base;

public enum Environment {

    DEVELOP,

    DEBUG,

    TEST,

    PRODUCT, Environment;

    private static Environment environment = DEVELOP;

    private static boolean active = true;

    private static boolean started = true;

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        Environment.environment = environment;
    }

    public static boolean isActive() {
        return active && started;
    }

    public static void setActive(boolean active) {
        Environment.active = active;
    }

    public static boolean isStarted() {
        return started;
    }

    public static void setStarted(boolean started) {
        Environment.started = started;
    }

    public static void throwable(Throwable e) {
        if (Environment.getEnvironment() == Environment.DEVELOP) {
            e.printStackTrace();
        }
    }

    public static boolean isDevelop() {
        return environment == DEVELOP;
    }
}
