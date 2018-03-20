/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月28日 下午4:15:38
 */
package com.absir.aserv.developer;

import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelObject;
import com.absir.orm.value.JoEntity;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings("unchecked")
@Inject
public class Scenario {

    public static final String SCENARIO = "SCENARIO";
    private static final String JOENTITY = "joEntity";

    private static final String SCENARIO_NAMES = Scenario.class.getName() + "@SCENARIO_NAMES";

    private static String DeveloperScenarioStacks = Scenario.class.getName() + "_STACKS";

    public static Map<String, Stack<Object>> getDeveloperStatcks(ServletRequest request) {
        Object stacksObject = request.getAttribute(DeveloperScenarioStacks);
        if (stacksObject != null && stacksObject instanceof Map) {
            return (Map<String, Stack<Object>>) stacksObject;
        }

        Map<String, Stack<Object>> developerStatcks = new HashMap<String, Stack<Object>>();
        request.setAttribute(DeveloperScenarioStacks, developerStatcks);
        return developerStatcks;
    }

    public static void push(String name, Object value, ServletRequest request) {
        Map<String, Stack<Object>> developerStatcks = getDeveloperStatcks(request);
        Stack<Object> stack = developerStatcks.get(name);
        if (stack == null) {
            stack = new Stack<Object>();
            developerStatcks.put(name, stack);
        }

        stack.push(value);
    }

    public static Object peek(String name, ServletRequest request) {
        Stack<Object> stack = getDeveloperStatcks(request).get(name);
        return stack == null || stack.size() <= 0 ? null : stack.peek();
    }

    public static Object pop(String name, ServletRequest request) {
        Stack<Object> stack = getDeveloperStatcks(request).get(name);
        return stack == null || stack.size() <= 0 ? null : stack.pop();
    }

    public static void set(String value, ServletRequest request) {
        push(SCENARIO, value, request);
    }

    public static String get(ServletRequest request) {
        return (String) peek(SCENARIO, request);
    }

    public static String pop(ServletRequest request) {
        return (String) pop(SCENARIO, request);
    }

    public static void pushJoEntity(JoEntity value, ServletRequest request) {
        push(JOENTITY, value, request);
    }

    public static JoEntity getJoEntity(ServletRequest request) {
        return (JoEntity) peek(JOENTITY, request);
    }

    public static JoEntity popJoEntity(ServletRequest request) {
        return (JoEntity) pop(JOENTITY, request);
    }

    public static boolean requestName(ServletRequest request, String name) {
        return KernelObject.equals(get(request), name);
    }

}
