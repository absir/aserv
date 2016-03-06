/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-28 下午6:51:32
 */
package com.absir.aserv.system.bean;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.proxy.JiUpdate;

/**
 * @author absir
 * 
 */
@Entity
public class JUpdateXls extends JbBase implements JiUpdate {

	@EmbeddedId
	private JEmbedSS id;

	/** updateTime */
	private long updateTime;

	/** serialize */
	@Lob
	private byte[] serialize;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.system.bean.base.JbBase#getId()
	 */
	@Override
	public Serializable getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(JEmbedSS id) {
		this.id = id;
	}

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
	 * @return the serialize
	 */
	public byte[] getSerialize() {
		return serialize;
	}

	/**
	 * @param serialize
	 *            the serialize to set
	 */
	public void setSerialize(byte[] serialize) {
		this.serialize = serialize;
	}
}
