/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-23 下午5:41:41
 */
package com.absir.aserv.developer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.ScripteNode;

import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang.ObjectEntry;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class DeveloperGenerator {

	/** gDefines */
	private List<ObjectEntry<String, Boolean>> gDefines = new ArrayList<ObjectEntry<String, Boolean>>();

	/** GENERATOR_STACK_KEY */
	private static final String GENERATOR_STACK_KEY = DeveloperGenerator.class.getName() + "GENERATORS";

	/**
	 * @param request
	 * @return
	 */
	public static DeveloperGenerator getDeveloperGenerator(ServletRequest request) {
		Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
		return generators == null ? null : generators.peek();
	}

	/**
	 * @param request
	 * @return
	 */
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

	/**
	 * @param request
	 */
	protected static void popDeveloperGenerator(ServletRequest request) {
		Stack<DeveloperGenerator> generators = (Stack<DeveloperGenerator>) request.getAttribute(GENERATOR_STACK_KEY);
		generators.pop();
	}

	/**
	 * @param gString
	 */
	protected void addGeneratorDefine(String gDefine) {
		gDefines.add(new ObjectEntry<String, Boolean>(gDefine, false));
	}

	/**
	 * @param identifier
	 * @param out
	 */
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

	/**
	 * @param identifier
	 * @param element
	 * @return
	 */
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
