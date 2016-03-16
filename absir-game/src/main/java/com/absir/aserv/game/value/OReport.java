/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-21 下午4:29:23
 */
package com.absir.aserv.game.value;

import com.absir.aserv.system.bean.dto.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

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

    public long getBegan() {
        return began;
    }

    public void setBegan(long began) {
        this.began = began;
    }

    public EResult getResult() {
        return result;
    }

    public void setResult(EResult result) {
        this.result = result;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

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
