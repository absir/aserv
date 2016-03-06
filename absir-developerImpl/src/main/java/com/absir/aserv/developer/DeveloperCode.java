/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer;

/**
 * @author absir
 * 
 */
public class DeveloperCode {

	/** SCRIPT_BEGIN */
	public static final String SCRIPT_BEGIN = "<%";

	/** SCRIPT_END */
	public static final String SCRIPT_END = "%>";

	/** PRINT_BEGIN */
	public static final String PRINT_BEGIN = "<%=";

	/** BLOCK_BEGIN */
	public static final String BLOCK_BEGIN = "{";

	/** BLOCK_END */
	public static final String BLOCK_END = "}";

	public static final String LINE_BREAK = "\r\n";

	/**
	 * @param jspfile
	 * @return
	 */
	public static String include(String jspfile) {
		return "<%@ include file=\"" + jspfile + "\"%>" + LINE_BREAK;
	}

	/**
	 * @param jspfile
	 * @return
	 */
	public static String jspInclude(String jspfile) {
		return "<jsp:include page=\"" + jspfile + "\"/>" + LINE_BREAK;
	}

	/**
	 * @param jspfile
	 * @param flush
	 * @return
	 */
	public static String jspInclude(String jspfile, boolean flush) {
		return "<jsp:include page=\"" + jspfile + "\" flush=\"" + String.valueOf(flush) + "\"/>" + LINE_BREAK;
	}

	/**
	 * @param cls
	 * @return
	 */
	public static String importClass(String cls) {
		return "<%@ page import=\"" + cls + "\"%>" + LINE_BREAK;
	}

	/**
	 * @return
	 */
	public static String scriptBegin() {
		return SCRIPT_BEGIN;
	}

	/**
	 * @return
	 */
	public static String scriptEnd() {
		return SCRIPT_END + LINE_BREAK;
	}

	/**
	 * @return
	 */
	public static String printBegin() {
		return PRINT_BEGIN;
	}

	/**
	 * @return
	 */
	public static String blockBegin() {
		return BLOCK_BEGIN;
	}

	/**
	 * @return
	 */
	public static String blockEnd() {
		return BLOCK_END + LINE_BREAK;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String script(String expression) {
		return SCRIPT_BEGIN + expression + SCRIPT_END;
	}

	public static String scriptln(String expression) {
		return script(expression) + LINE_BREAK;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String print(String expression) {
		return PRINT_BEGIN + expression + SCRIPT_END;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String println(String expression) {
		return print(expression) + LINE_BREAK;
	}

	/**
	 * @param script
	 * @return
	 */
	public static String unPrint(String script) {
		int length = script.length();
		if (length > 3) {
			if (script.charAt(0) == '<' && script.charAt(1) == '%' && script.charAt(2) == '=') {
				script = script.substring(3, length - 2);
			}
		}

		return script;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String bracket(String expression) {
		return "(" + expression + ")";
	}

	/**
	 * @param cls
	 * @param name
	 * @return
	 */
	public static String define(String cls, String name) {
		return cls + " " + name + ";" + LINE_BREAK;
	}

	/**
	 * @param cls
	 * @param name
	 * @param expression
	 * @return
	 */
	public static String define(String cls, String name, String expression) {
		return cls + " " + name + " = " + expression + ";" + LINE_BREAK;
	}

	/**
	 * @param cls
	 * @param name
	 * @param expression
	 * @return
	 */
	public static String defineCast(String cls, String name, String expression) {
		return cls + " " + name + " = (" + cls + ") " + expression + ";" + LINE_BREAK;
	}

	/**
	 * @param obj
	 * @param method
	 * @param parameters
	 * @return
	 */
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

	/**
	 * @param expression
	 * @return
	 */
	public static String line(String expression) {
		return expression + LINE_BREAK;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String lineCode(String expression) {
		return expression + ";" + LINE_BREAK;
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String unExpression(String expression) {
		return expression.substring(2, expression.length() - 1);
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String escape(String expression) {
		return expression.replace("\"", "\\\"");
	}

	/**
	 * @param value
	 * @param var
	 * @return
	 */
	public static String set(String value, String var) {
		return set(value, var, null);
	}

	/**
	 * @param value
	 * @param var
	 * @param scope
	 * @return
	 */
	public static String set(String value, String var, String scope) {
		return "<c:set value=\"" + value + "\" var=\"" + var + "\"" + (scope == null ? "" : " scope=\"" + scope + "\"") + " />";
	}

}
