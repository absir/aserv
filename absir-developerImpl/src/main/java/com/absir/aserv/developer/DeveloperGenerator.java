/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-23 下午5:41:41
 */
package com.absir.aserv.developer;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.ScripteNode;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class DeveloperGenerator {

    private static final String GENERATOR_STACK_KEY = DeveloperGenerator.class.getName() + "GENERATORS";

    private List<ObjectEntry<String, Boolean>> gDefines = new ArrayList<ObjectEntry<String, Boolean>>();

    public static DeveloperGenerator getDeveloperGenerator(ServletRequest request) {
        Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
        return generators == null ? null : generators.peek();
    }

    protected static DeveloperGenerator pushDeveloperGenerator(ServletRequest request) {
        if (request == null) {
            return null;
        }

        Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
        if (generators == null) {
            generators = new Stack<DeveloperGenerator>();
            request.setAttribute(GENERATOR_STACK_KEY, generators);
        }

        DeveloperGenerator generator = new DeveloperGenerator();
        generators.push(generator);
        return generator;

    }

    protected static void popDeveloperGenerator(ServletRequest request) {
        Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
        generators.pop();
    }

    protected void addGeneratorDefine(String gDefine) {
        gDefines.add(new ObjectEntry<String, Boolean>(gDefine, false));
    }

    public boolean print(String identifier, JspWriter out) {
        try {
            for (ObjectEntry<String, Boolean> gDefine : gDefines) {
                if (gDefine.getKey().indexOf(identifier) >= 0) {
                    if (!gDefine.getValue()) {
                        out.println(gDefine.getKey());
                    }

                    return true;
                }
            }

        } catch (IOException e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean append(String identifier, Element element) {
        try {
            for (ObjectEntry<String, Boolean> gDefine : gDefines) {
                if (gDefine.getKey().indexOf(identifier) >= 0) {
                    if (!gDefine.getValue()) {
                        element.appendChild(ScripteNode.node(gDefine.getKey()));
                    }

                    return true;
                }
            }

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
