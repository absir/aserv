/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-23 下午2:59:01
 */
package com.absir.aserv.system.domain;

import java.util.List;
import java.util.Map;

/**
 * @author absir
 * 
 */
public class DCondition {

	/** updateTime */
	private long updateTime;

	/** strategy */
	private boolean strategy;

	/** strageties */
	private Map<String, List<Object>> mapStrategies;

	/** createStrategies */
	private Map<String, List<Object>> createStrategies;

	/**
	 * @return the updateTime
	 */
	public long getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the strategy
	 */
	public boolean isStrategy() {
		return strategy;
	}

	/**
	 * @param strategy
	 *            the strategy to set
	 */
	public void setStrategy(boolean strategy) {
		this.strategy = strategy;
	}

	/**
	 * @return the mapStrategies
	 */
	public Map<String, List<Object>> getMapStrategies() {
		return mapStrategies;
	}

	/**
	 * @param mapStrategies
	 *            the mapStrategies to set
	 */
	public void setMapStrategies(Map<String, List<Object>> mapStrategies) {
		this.mapStrategies = mapStrategies;
	}

	/**
	 * @return the createStrategies
	 */
	public Map<String, List<Object>> getCreateStrategies() {
		return createStrategies;
	}

	/**
	 * @param createStrategies
	 *            the createStrategies to set
	 */
	public void setCreateStrategies(Map<String, List<Object>> createStrategies) {
		this.createStrategies = createStrategies;
	}

}
