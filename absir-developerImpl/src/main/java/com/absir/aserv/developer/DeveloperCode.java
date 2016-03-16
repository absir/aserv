/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer;

public class DeveloperCode {

    public static final String SCRIPT_BEGIN = "<%";

    public static final String SCRIPT_END = "%>";

    public static final String PRINT_BEGIN = "<%=";

    public static final String BLOCK_BEGIN = "{";

    public static final String BLOCK_END = "}";

    public static final String LINE_BREAK = "\r\n";

    public static String include(String jspfile) {
        return "<%@ include file=\"" + jspfile + "\"%>" + LINE_BREAK;
    }

    public static String jspInclude(String jspfile) {
        return "<jsp:include page=\"" + jspfile + "\"/>" + LINE_BREAK;
    }

    public static String jspInclude(String jspfile, boolean flush) {
        return "<jsp:include page=\"" + jspfile + "\" flush=\"" + String.valueOf(flush) + "\"/>" + LINE_BREAK;
    }

    public static String importClass(String cls) {
        return "<%@ page import=\"" + cls + "\"%>" + LINE_BREAK;
    }

    public static String scriptBegin() {
        return SCRIPT_BEGIN;
    }

    public static String scriptEnd() {
        return SCRIPT_END + LINE_BREAK;
    }

    public static String printBegin() {
        return PRINT_BEGIN;
    }

    public static String blockBegin() {
        return BLOCK_BEGIN;
    }

    public static String blockEnd() {
        return BLOCK_END + LINE_BREAK;
    }

    public static String script(String expression) {
        return SCRIPT_BEGIN + expression + SCRIPT_END;
    }

    public static String scriptln(String expression) {
        return script(expression) + LINE_BREAK;
    }

    public static String print(String expression) {
        return PRINT_BEGIN + expression + SCRIPT_END;
    }

    public static String println(String expression) {
        return print(expression) + LINE_BREAK;
    }

    public static String unPrint(String script) {
        int length = script.length();
        if (length > 3) {
            if (script.charAt(0) == '<' && script.charAt(1) == '%' && script.charAt(2) == '=') {
                script = script.substring(3, length - 2);
            }
        }

        return script;
    }

    public static String bracket(String expression) {
        return "(" + expression + ")";
    }

    public static String define(String cls, String name) {
        return cls + " " + name + ";" + LINE_BREAK;
    }

    public static String define(String cls, String name, String expression) {
        return cls + " " + name + " = " + expression + ";" + LINE_BREAK;
    }

    public static String defineCast(String cls, String name, String expression) {
        return cls + " " + name + " = (" + cls + ") " + expression + ";" + LINE_BREAK;
    }

    public static String sendMethod(String obj, String method, String... parameters) {
        StringBuilder builder = new StringBuilder(obj + '.' + method + '(');
        boolean next = false;
        for (String param : parameters) {
            builder.append(param);
            if (next) {
                builder.append(',');
            } else {
                next = false;
            }
        }
        builder.append(')');
        return builder.toString();
    }

    public static String line(String expression) {
        return expression + LINE_BREAK;
    }

    public static String lineCode(String expression) {
        return expression + ";" + LINE_BREAK;
    }

    public static String unExpression(String expression) {
        return expression.substring(2, expression.length() - 1);
    }

    public static String escape(String expression) {
        return expression.replace("\"", "\\\"");
    }

    public static String set(String value, String var) {
        return set(value, var, null);
    }

    public static String set(String value, String var, String scope) {
        return "<c:set value=\"" + value + "\" var=\"" + var + "\"" + (scope == null ? "" : " scope=\"" + scope + "\"") + " />";
    }

}
