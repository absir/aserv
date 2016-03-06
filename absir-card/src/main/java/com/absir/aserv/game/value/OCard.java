/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-5 下午3:44:12
 */
package com.absir.aserv.game.value;

import java.io.Serializable;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class OCard<C extends OCard, F extends OFight> extends OObject<C> {

	// 暂停回合数
	protected int paused;

	// 暂停回合
	private static final String PAUSED = "P";

	/**
	 * @param id
	 */
	public OCard(Serializable id) {
		super(id);
	}

	/**
	 * @return the paused
	 */
	public int getPaused() {
		return paused;
	}

	/**
	 * @param paused
	 *            the paused to set
	 */
	protected void setPaused(int paused) {
		this.paused = paused;
	}

	/**
	 * @param target
	 * @param paused
	 * @param result
	 */
	public void paused(C target, int paused, IResult result) {
		addReportDetail(target, PAUSED, paused);
		paused += this.paused;
		setPaused(paused);
	}

	/**
	 * 获取基础生命
	 * 
	 * @return
	 */
	public abstract int baseHp();

	/**
	 * 获取基础攻击力
	 * 
	 * @return
	 */
	public abstract int baseAtk();

	/**
	 * 当前战斗
	 * 
	 * @return
	 */
	public abstract F currentFight();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.game.value.OObject#atk()
	 */
	@Override
	public boolean atk() {
		return paused == 0 || --paused == 0;
	}

	/**
	 * 是否在TargetCards
	 * 
	 * @return
	 */
	public boolean inTarget() {
		for (OCard card : currentFight().getCards()) {
			if (this == card) {
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.aserv.game.value.OObject#fetchTarget()
	 */
	@Override
	public C fetchTarget() {
		for (OCard card : inTarget() ? currentFight().getCards() : currentFight().getTargetCards()) {
			if (card.hp > 0) {
				if (target == null) {
					target = (C) card;
				}

				if (!card.invincible) {
					return (C) card;
				}
			}
		}

		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.game.value.OObject#addReportDetail(com.absir.aserv
	 * .game.value.OObject, java.lang.String, java.lang.Object)
	 */
	@Override
	public void addReportDetail(C target, String effect, Object parameters) {
		currentFight().addReportDetail(getId(), target == null ? null : new Serializable[] { target.getId() }, effect, parameters);
	}
}
