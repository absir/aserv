/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-23 下午5:41:41
 */
package com.absir.aserv.developer;

import com.absir.aserv.support.developer.IField;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.ScriptNode;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class DeveloperGenerator {

    private static final String GENERATOR_STACK_KEY = DeveloperGenerator.class.getName() + "GENERATOR_STACK";

    protected DeveloperGenerator readyGenerator;

    protected KernelLang.PropertyFilter propertyFilter;

    protected List<ObjectEntry<String, Boolean>> gDefines = new ArrayList<ObjectEntry<String, Boolean>>();

    protected Map<String, List<ObjectEntry<String, Boolean>>> tagMapGDefines;

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

        DeveloperGenerator developerGenerator = generators.empty() ? null : generators.peek();
        DeveloperGenerator generator = developerGenerator == null ? null : developerGenerator.readyGenerator;
        if (generator == null) {
            generator = new DeveloperGenerator();
        }

        generators.push(generator);
        request.setAttribute("generator", generator);
        if (developerGenerator != null) {
            developerGenerator.readyGenerator = null;
        }

        return generator;
    }

    protected static void popDeveloperGenerator(ServletRequest request) {
        Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
        generators.pop();
        if (generators.empty()) {
            request.removeAttribute("generator");

        } else {
            request.setAttribute("generator", generators.peek());
        }
    }

    public DeveloperGenerator readyDeveloperGenerator(ServletRequest request) {
        if (readyGenerator == null) {
            readyGenerator = new DeveloperGenerator();
        }

        return readyGenerator;
    }

    public KernelLang.PropertyFilter filter() {
        if (propertyFilter == null) {
            propertyFilter = new KernelLang.PropertyFilter();
        }

        return propertyFilter;
    }

    public boolean allow(IField field) {
        if (field.getEditable() == JeEditable.DISABLE) {
            return false;
        }

        if (propertyFilter != null) {
            return propertyFilter.allow(field.getInclude(), field.getExclude()) && propertyFilter.isMatchPath(field.getName());
        }

        return true;
    }

    protected void addGeneratorDefine(String gDefine) {
        gDefines.add(new ObjectEntry<String, Boolean>(gDefine, false));
    }

    public void setTag(String tag) {
        if (tagMapGDefines != null) {
            if (tag == null) {
                tag = "";
            }

            List<ObjectEntry<String, Boolean>> defines = tagMapGDefines.get(tag);
            if (defines != null) {
                if (tag.length() > 0) {
                    if (!tagMapGDefines.containsKey("")) {
                        tagMapGDefines.put("", gDefines);
                    }
                }

                gDefines = defines;
            }
        }
    }

    public boolean print(String identifier, Writer out) {
        try {
            for (ObjectEntry<String, Boolean> gDefine : gDefines) {
                if (gDefine.getKey().indexOf(identifier) >= 0) {
                    if (!gDefine.getValue()) {
                        gDefine.setValue(true);
                        out.append(gDefine.getKey());
                        out.append("\r\n");
                    }

                    return true;
                }
            }

        } catch (IOException e) {
            Environment.throwable(e);
        }

        return false;
    }

    public boolean append(String identifier, Element element) {
        try {
            for (ObjectEntry<String, Boolean> gDefine : gDefines) {
                if (gDefine.getKey().indexOf(identifier) >= 0) {
                    if (!gDefine.getValue()) {
                        gDefine.setValue(true);
                        element.appendChild(ScriptNode.node(gDefine.getKey()));
                    }

                    return true;
                }
            }

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return false;
    }
}
