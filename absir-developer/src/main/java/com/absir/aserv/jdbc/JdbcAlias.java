/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-22 上午9:39:40
 */
package com.absir.aserv.jdbc;

import com.absir.core.kernel.KernelString;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class JdbcAlias {

    public static final String ALIAS = "o";

    public static final String JOIN = "JOIN";

    private String alias = ALIAS;

    private String joinAlias = "";

    private Stack<String> aliasStack = new Stack<String>();

    private Map<String, String> propertyPathMapAlias = new HashMap<String, String>();

    public String getAlias() {
        return alias;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public void setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
    }

    public void pushAlias() {
        aliasStack.push(alias);
    }

    public void popAlias() {
        alias = aliasStack.pop();
        if (alias == null) {
            alias = ALIAS;
        }
    }

    public Map<String, String> getPropertyPathMapAlias() {
        return propertyPathMapAlias;
    }

    public String getProperyName(String propertyName) {
        return getAliasProperyName(alias, propertyName);
    }

    public String getAliasProperyName(String alias, String propertyName) {
        if (alias != null) {
            propertyName = alias + "." + propertyName;
        }

        String nextAlias = propertyPathMapAlias.get(propertyName);
        if (nextAlias == null) {
            nextAlias = KernelString.nextSequenceString(this.alias);
            this.alias = nextAlias;
            propertyPathMapAlias.put(propertyName, nextAlias);
        }

        return nextAlias;
    }

    public String joinProperyName(String propertyName) {
        return joinAliasProperyName(alias, propertyName);
    }

    public String joinProperyName(String join, String propertyName) {
        return joinAliasProperyNameQueue(join, alias, propertyName, null);
    }

    public String joinProperyNameQueue(String propertyName, String queue) {
        return joinAliasProperyNameQueue(JOIN, alias, propertyName, queue);
    }

    public String joinProperyNameQueue(String join, String propertyName, String queue) {
        return joinAliasProperyNameQueue(join, alias, propertyName, queue);
    }

    public String joinAliasProperyName(String alias, String propertyName) {
        return joinAliasProperyName(JOIN, alias, propertyName);
    }

    public String joinAliasProperyName(String join, String alias, String propertyName) {
        return joinAliasProperyNameQueue(join, alias, propertyName, null);
    }

    public String joinAliasProperyNameQueue(String alias, String propertyName, String queue) {
        return joinAliasProperyNameQueue(JOIN, alias, propertyName, queue);
    }

    public String joinAliasProperyNameQueue(String join, String alias, String propertyName, String queue) {
        if (alias != null) {
            propertyName = alias + "." + propertyName;
        }

        String nextAlias = propertyPathMapAlias.get(propertyName);
        if (nextAlias == null) {
            nextAlias = KernelString.nextSequenceString(alias);
            propertyPathMapAlias.put(propertyName, nextAlias);
            joinAlias += " " + join + " " + propertyName + " " + nextAlias;
            if (queue != null) {
                joinAlias += " " + queue;
            }
        }

        return nextAlias;
    }
}
