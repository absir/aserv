/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 下午2:58:35
 */
package com.absir.aserv.game.value;

import java.io.Serializable;

import com.absir.aserv.game.utils.GameUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes" })
public abstract class OFight<C extends OCard, R extends OReportResult> {

	// 战报
	@JsonIgnore
	protected R reportResult;

	/**
	 * 
	 */
	public OFight() {
		reportResult = createReportResult();
	}

	/**
	 * @return the report
	 */
	public R getReportResult() {
		return reportResult;
	}

	/**
	 * @param time
	 */
	public synchronized final OReport step(long time) {
		reportResult.began(time);
		for (OCard card : getCards()) {
			card.step(time, reportResult);
		}

		// 反转战斗结果
		GameUtils.revert(reportResult);
		for (OCard card : getTargetCards()) {
			card.step(time, reportResult);
		}

		// 反转战斗结果
		GameUtils.revert(reportResult);

		// 处理战报
		return dealReportResult(reportResult);
	}

	/**
	 * 获取己方单位
	 * 
	 * @return
	 */
	public abstract C[] getCards();

	/**
	 * 获取对方单位
	 * 
	 * @return
	 */
	public abstract C[] getTargetCards();

	/**
	 * @param card
	 * @param targets
	 * @param effect
	 * @param parameters
	 */
	public void addReportDetail(Serializable self, Serializable[] targets, String effect, Object parameters) {
		OReportDetail reportDetail = createReportDetail();
		reportDetail.setSelf(self);
		reportDetail.setTargets(targets);
		reportDetail.setEffect(effect);
		reportDetail.setEffectData(parameters);
		reportResult.addReportDetail(reportDetail);
	}

	/**
	 * 创建战报
	 * 
	 * @return
	 */
	protected abstract R createReportResult();

	/**
	 * 创建详细战报
	 * 
	 * @return
	 */
	protected OReportDetail createReportDetail() {
		return new OReportDetail();
	}

	/**
	 * 处理战报
	 * 
	 * @param reportResult
	 * @return
	 */
	public abstract R dealReportResult(R reportResult);

}
