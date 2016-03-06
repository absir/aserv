/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-21 下午4:29:23
 */
package com.absir.aserv.game.value;

import java.util.ArrayList;
import java.util.List;

import com.absir.aserv.system.bean.dto.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author absir
 * 
 */
public class OReport {

	// 战斗开始
	private long began;

	// 战斗结果
	@JsonSerialize(using = EnumSerializer.class)
	private EResult result;

	// 战斗结果数据
	private Object resultData;

	// 详细战报
	private List<OReportDetail> reportDetails = new ArrayList<OReportDetail>();

	/**
	 * @return the began
	 */
	public long getBegan() {
		return began;
	}

	/**
	 * @param began
	 *            the began to set
	 */
	public void setBegan(long began) {
		this.began = began;
	}

	/**
	 * @return the result
	 */
	public EResult getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(EResult result) {
		this.result = result;
	}

	/**
	 * @return the resultData
	 */
	public Object getResultData() {
		return resultData;
	}

	/**
	 * @param resultData
	 *            the resultData to set
	 */
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

	/**
	 * @return the reportDetails
	 */
	public List<OReportDetail> getReportDetails() {
		return reportDetails;
	}

	/**
	 * 开始新战报
	 * 
	 * @return the reportDetails
	 */
	public void began(long time) {
		began = time;
		result = EResult.CONTINUE;
		resultData = null;
		reportDetails.clear();
	}

	/**
	 * 添加详细战报
	 * 
	 * @param reportDetail
	 */
	public void addReportDetail(OReportDetail reportDetail) {
		// reportDetail.setDepth(depth);
		reportDetails.add(reportDetail);
	}
}
