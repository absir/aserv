/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-22 上午9:39:40
 */
package com.absir.aserv.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class JdbcAlias {

	/** ALIAS */
	public static final String ALIAS = "o";

	/** JOIN */
	public static final String JOIN = "JOIN";

	/** alias */
	private String alias = ALIAS;

	/** joinAlias */
	private String joinAlias = "";

	/** aliasStack */
	private Stack<String> aliasStack = new Stack<String>();

	/** propertyPathMapAlias */
	private Map<String, String> propertyPathMapAlias = new HashMap<String, String>();

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @return the joinAlias
	 */
	public String getJoinAlias() {
		return joinAlias;
	}

	/**
	 * @param joinAlias
	 *            the joinAlias to set
	 */
	public void setJoinAlias(String joinAlias) {
		this.joinAlias = joinAlias;
	}

	/**
	 * 
	 */
	public void pushAlias() {
		aliasStack.push(alias);
	}

	/**
	 * 
	 */
	public void popAlias() {
		alias = aliasStack.pop();
		if (alias == null) {
			alias = ALIAS;
		}
	}

	/**
	 * @return the propertyPathMapAlias
	 */
	public Map<String, String> getPropertyPathMapAlias() {
		return propertyPathMapAlias;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	public String getProperyName(String propertyName) {
		return getAliasProperyName(alias, propertyName);
	}

	/**
	 * @param alias
	 * @param propertyName
	 * @return
	 */
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

	/**
	 * @param propertyName
	 * @return
	 */
	public String joinProperyName(String propertyName) {
		return joinAliasProperyName(alias, propertyName);
	}

	/**
	 * @param join
	 * @param propertyName
	 * @return
	 */
	public String joinProperyName(String join, String propertyName) {
		return joinAliasProperyNameQueue(join, alias, propertyName, null);
	}

	/**
	 * @param propertyName
	 * @param queue
	 * @return
	 */
	public String joinProperyNameQueue(String propertyName, String queue) {
		return joinAliasProperyNameQueue(JOIN, alias, propertyName, queue);
	}

	/**
	 * @param join
	 * @param propertyName
	 * @param queue
	 * @return
	 */
	public String joinProperyNameQueue(String join, String propertyName, String queue) {
		return joinAliasProperyNameQueue(join, alias, propertyName, queue);
	}

	/**
	 * @param alias
	 * @param propertyName
	 * @return
	 */
	public String joinAliasProperyName(String alias, String propertyName) {
		return joinAliasProperyName(JOIN, alias, propertyName);
	}

	/**
	 * @param join
	 * @param alias
	 * @param propertyName
	 * @return
	 */
	public String joinAliasProperyName(String join, String alias, String propertyName) {
		return joinAliasProperyNameQueue(join, alias, propertyName, null);
	}

	/**
	 * @param alias
	 * @param propertyName
	 * @param queue
	 * @return
	 */
	public String joinAliasProperyNameQueue(String alias, String propertyName, String queue) {
		return joinAliasProperyNameQueue(JOIN, alias, propertyName, queue);
	}

	/**
	 * @param join
	 * @param alias
	 * @param propertyName
	 * @param queue
	 * @return
	 */
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
