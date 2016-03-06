/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-12-25 下午3:25:07
 */
package com.absir.server.route;

import java.util.List;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMatcher;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;

/**
 * @author absir
 * 
 */
public class RouteMatcher extends InMatcher {

	/** routeAction */
	private RouteAction routeAction;

	/** inMethods */
	private InMethod[] inMethods;

	/** parameterNames */
	private String[] parameterNames;

	/** routeParameter */
	private RouteParameter routeParameter;

	/**
	 * @param routeAction
	 * @param mapping
	 * @param suffix
	 * @param inMethods
	 * @param parameterNames
	 * @param routeParameter
	 */
	public RouteMatcher(RouteAction routeAction, String mapping, String suffix, List<InMethod> inMethods, String[] parameterNames, RouteParameter routeParameter) {
		super(mapping, suffix);
		this.routeAction = routeAction;
		inMethods = routeAction.getRouteMethod().resolveMethods(inMethods);
		this.inMethods = inMethods == null || inMethods.isEmpty() ? null : KernelCollection.toArray(inMethods, InMethod.class);
		this.parameterNames = parameterNames;
		this.routeParameter = routeParameter;
	}

	/**
	 * @return the routeAction
	 */
	public RouteAction getRouteAction() {
		return routeAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.server.in.InMatcher#getParameterLength()
	 */
	@Override
	public int getParameterLength() {
		return parameterNames == null ? 0 : parameterNames.length;
	}

	/**
	 * @return the routeParameter
	 */
	public RouteParameter getRouteParameter() {
		return routeParameter;
	}

	/**
	 * @return
	 */
	public int getInMethodLength() {
		return inMethods == null ? 0 : inMethods.length;
	}

	/**
	 * @param inMethod
	 * @return
	 */
	public boolean find(InMethod inMethod) {
		return inMethod == null || inMethods == null ? true : KernelArray.contain(inMethods, inMethod);
	}

	/**
	 * @param parameters
	 * @return
	 */
	public InModel find(String[] parameters) {
		int length = parameters.length;
		if (parameterNames == null || length != parameterNames.length) {
			return null;

		} else {
			InModel model = new InModel();
			for (int i = 0; i < length; i++) {
				model.put(parameterNames[i], parameters[i]);
			}

			return model;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + (inMethods == null ? "" : KernelString.implode(inMethods, ',')) + "]" + (getMapping() == null ? "" : new String(getMapping()))
				+ (routeParameter == null ? "" : routeParameter.toParameterPath(getParameterLength())) + (getSuffix() == null ? "" : new String(getSuffix())) + " => "
				+ routeAction.getRouteMethod().getMethod();
	}
}
