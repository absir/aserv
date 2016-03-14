/**
 * Copyright 2014 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2014-1-10 下午1:50:51
 */
package com.absir.core.base;

/**
 * @author absir
 */
public enum Environment {

    /**
     * DEVELOP
     */
    DEVELOP,

    /**
     * DEBUG
     */
    DEBUG,

    /**
     * TEST
     */
    TEST,

    /**
     * PRODUCT
     */
    PRODUCT;

    /**
     * environment
     */
    private static Environment environment = DEVELOP;

    /**
     * active
     */
    private static boolean active = true;

    /**
     * started
     */
    private static boolean started = true;

    /**
     * @return the environment
     */
    public static Environment getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public static void setEnvironment(Environment environment) {
        Environment.environment = environment;
    }

    /**
     * @return the active
     */
    public static boolean isActive() {
        return active && started;
    }

    /**
     * @param active the active to set
     */
    public static void setActive(boolean active) {
        Environment.active = active;
    }

    /**
     * @return the started
     */
    public static boolean isStarted() {
        return started;
    }

    /**
     * @param started the started to set
     */
    public static void setStarted(boolean started) {
        Environment.started = started;
    }
}
