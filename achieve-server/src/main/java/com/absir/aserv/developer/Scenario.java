/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月28日 下午4:15:38
 */
package com.absir.aserv.developer;

import com.absir.bean.inject.value.Inject;
import com.absir.orm.value.JoEntity;

import javax.servlet.ServletRequest;
import java.util.*;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
@Inject
public class Scenario {

    public static final String SCENARIO = "SCENARIO";
    private static final String JOENTITY = "joEntity";
    /**
     * SCENARIO_NAMES
     */
    private static final String SCENARIO_NAMES = Scenario.class.getName() + "@SCENARIO_NAMES";
    /**
     * DeveloperScenarioStacks
     */
    private static String DeveloperScenarioStacks = Scenario.class.getName() + "_STACKS";

    /**
     * @param request
     * @return
     */
    public static Map<String, Stack<Object>> getDeveloperStatcks(ServletRequest request) {
        Object stacksObject = request.getAttribute(DeveloperScenarioStacks);
        if (stacksObject != null && stacksObject instanceof Map) {
            return (Map<String, Stack<Object>>) stacksObject;
        }

        Map<String, Stack<Object>> developerStatcks = new HashMap<String, Stack<Object>>();
        request.setAttribute(DeveloperScenarioStacks, developerStatcks);
        return developerStatcks;
    }

    /**
     * @param name
     * @param value
     * @param request
     */
    public static void push(String name, Object value, ServletRequest request) {
        Map<String, Stack<Object>> developerStatcks = getDeveloperStatcks(request);
        Stack<Object> stack = developerStatcks.get(name);
        if (stack == null) {
            stack = new Stack<Object>();
            developerStatcks.put(name, stack);
        }

        stack.push(value);
    }

    /**
     * @param name
     * @param request
     * @return
     */
    public static Object peek(String name, ServletRequest request) {
        Stack<Object> stack = getDeveloperStatcks(request).get(name);
        return stack == null || stack.size() <= 0 ? null : stack.peek();
    }

    /**
     * @param name
     * @param request
     * @return
     */
    public static Object pop(String name, ServletRequest request) {
        Stack<Object> stack = getDeveloperStatcks(request).get(name);
        return stack == null || stack.size() <= 0 ? null : stack.pop();
    }

    /**
     * @param value
     * @param request
     */
    public static void set(String value, ServletRequest request) {
        push(SCENARIO, value, request);
    }

    /**
     * @param request
     * @return
     */
    public static String get(ServletRequest request) {
        return (String) peek(SCENARIO, request);
    }

    /**
     * @param request
     * @return
     */
    public static String pop(ServletRequest request) {
        return (String) pop(SCENARIO, request);
    }

    /**
     * @param value
     */
    public static void pushJoEntity(JoEntity value, ServletRequest request) {
        push(JOENTITY, value, request);
    }

    /**
     * @param request
     * @return
     */
    public static JoEntity getJoEntity(ServletRequest request) {
        return (JoEntity) peek(JOENTITY, request);
    }

    /**
     * @param request
     * @return
     */
    public static JoEntity popJoEntity(ServletRequest request) {
        return (JoEntity) pop(JOENTITY, request);
    }

    /**
     * @param request
     * @param name
     * @return
     */
    public static boolean requestName(ServletRequest request, String name) {
        Object names = request.getAttribute(SCENARIO_NAMES);
        Set<Object> scenarioNames = null;
        if (names == null || !(names instanceof Set)) {
            scenarioNames = new HashSet<Object>();
            request.setAttribute(SCENARIO_NAMES, scenarioNames);

        } else {
            if (((Set<Object>) names).contains(name)) {
                return false;
            }

            scenarioNames = (Set<Object>) names;
        }

        scenarioNames.add(name);
        return true;
    }
}
