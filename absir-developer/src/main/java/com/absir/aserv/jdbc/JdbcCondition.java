/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-7-19 下午12:50:38
 */
package com.absir.aserv.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 * 
 */
public class JdbcCondition extends JdbcAlias {

	/** conditions */
	private List<Object> conditions = new ArrayList<Object>();

	/** cacheConditionProperties */
	private List<List<ConditionProperty>> cacheConditionProperties = new ArrayList<List<ConditionProperty>>();

	/** conditionProperties */
	private List<ConditionProperty> conditionProperties = new ArrayList<ConditionProperty>();

	/**
	 * @return the conditions
	 */
	public List<Object> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions
	 *            the conditions to set
	 */
	public void setConditions(List<Object> conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return
	 */
	public List<Object> getConditionList() {
		return getConditionList(conditions);
	}

	/**
	 * @return
	 */
	public String getPropertyAlias() {
		if (conditionProperties.size() > 0) {
			String alias = conditionProperties.get(conditionProperties.size() - 1).alias;
			if (alias != null) {
				return alias;
			}

			for (ConditionProperty conditionProperty : conditionProperties) {
				alias = conditionProperty.getPropertyAlias(this, alias);
			}

			return alias;
		}

		return ALIAS;
	}

	/**
	 * @param right
	 * @return
	 */
	public String getPropertyAlias(int right) {
		List<ConditionProperty> conditionProperties = this.conditionProperties;
		int size = cacheConditionProperties.size();
		while ((right -= conditionProperties.size()) > 0 && size > 0) {
			conditionProperties = cacheConditionProperties.get(--size);
		}

		if (right < 0) {
			right = -right - 1;
			String alias = conditionProperties.get(right).alias;
			if (alias != null) {
				return alias;
			}

			for (int i = 0; i <= right; i++) {
				alias = conditionProperties.get(i).getPropertyAlias(this, alias);
			}

			return alias;
		}

		return ALIAS;
	}

	/**
	 * @return
	 */
	public String getCurrentPropertyAlias() {
		if (conditionProperties.size() > 0) {
			return conditionProperties.get(conditionProperties.size() - 1).alias;
		}

		return ALIAS;
	}

	/**
	 * @param conditionProperty
	 */
	public void openProperty(ConditionProperty conditionProperty) {
		conditionProperties.add(conditionProperty);
	}

	/**
	 * 
	 */
	public void reopenAlias() {
		cacheConditionProperties.add(conditionProperties);
		conditionProperties = new ArrayList<ConditionProperty>();
	}

	/**
	 * 
	 */
	public void closeProperty() {
		if (conditionProperties.size() > 0) {
			conditionProperties.remove(conditionProperties.size() - 1);

		} else {
			if (cacheConditionProperties.size() > 0) {
				conditionProperties = cacheConditionProperties.remove(cacheConditionProperties.size() - 1);
			}
		}
	}

	/**
	 * 
	 */
	public void clearAlias() {
		cacheConditionProperties.clear();
		conditionProperties.clear();
	}

	/**
	 * @author absir
	 * 
	 */
	@SuppressWarnings("serial")
	public static class Conditions extends ArrayList<Object> {

		/** glue */
		private String glue;

		/** parent */
		private List<Object> parent;

		/**
		 * @param parent
		 */
		public Conditions(List<Object> parent) {
			this.parent = parent;
			parent.add(this);
		}

		/**
		 * @param parent
		 */
		public Conditions(String glue, List<Object> parent) {
			this(parent);
			this.glue = glue;
		}

		/**
		 * @param glue
		 * @param parent
		 * @param index
		 */
		public Conditions(String glue, List<Object> parent, int index) {
			this.parent = parent;
			parent.add(index, this);
			this.glue = glue;
		}

		/**
		 * @param glue
		 * @param parent
		 * @param conditions
		 */
		public Conditions(String glue, List<Object> parent, List<Object> conditions) {
			this(glue, parent);
			addAll(conditions);
		}

		/**
		 * @return the glue
		 */
		public String getGlue() {
			return glue;
		}

		/**
		 * @return the parent
		 */
		public List<Object> getParent() {
			return parent;
		}
	}

	/**
	 * @param conditions
	 * @return
	 */
	public static List<Object> getConditionList(List<Object> conditions) {
		List<Object> conditionList = new ArrayList<Object>();
		setConditionList(conditions, conditionList);
		return conditionList;
	}

	/**
	 * @param conditions
	 * @param conditionList
	 */
	private static void setConditionList(List<Object> conditions, List<Object> conditionList) {
		for (Object condition : conditions) {
			if (condition instanceof Conditions) {
				int left = conditionList.size();
				setConditionList((Conditions) condition, conditionList);
				int right = conditionList.size() - 2;
				if (right >= left) {
					String glue = ((Conditions) condition).glue;
					if (right == left) {
						if (glue != null) {
							conditionList.set(left, glue + conditionList.get(left));
						}

					} else {
						conditionList.set(left, (glue == null ? "(" : glue + " (") + conditionList.get(left));
						conditionList.set(right, conditionList.get(right) + ")");
					}
				}

			} else {
				conditionList.add(condition);
			}
		}
	}

	/**
	 * @author absir
	 * 
	 */
	public static class ConditionProperty {

		/** alias */
		protected String alias;

		/** propertyName */
		protected String propertyName;

		/**
		 * @param propertyName
		 */
		public ConditionProperty(String propertyName) {
			this.propertyName = propertyName;
		}

		/**
		 * @return the alias
		 */
		public String getAlias() {
			return alias;
		}

		/**
		 * @return the propertyName
		 */
		public String getPropertyName() {
			return propertyName;
		}

		/**
		 * @param jdbcAlias
		 * @param alias
		 * @return
		 */
		public String getPropertyAlias(JdbcAlias jdbcAlias, String alias) {
			if (this.alias == null) {
				this.alias = jdbcAlias.getAliasProperyName(alias, propertyName);
			}

			return this.alias;
		}
	}
}
