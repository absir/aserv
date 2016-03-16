/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-21 下午4:33:13
 */
package com.absir.aserv.game.value;

import java.io.Serializable;

/**
 * 详细战报
 *
 * @author absir
 */
public class OReportDetail {

    // 战报对象
    private Serializable self;

    // 目标对象
    private Serializable[] targets;

    // 战斗效果
    private String effect;

    // 战斗效果结果数据
    private Object effectData;

    public OReportDetail() {
    }

    public OReportDetail(Object effectData) {
        this.effectData = effectData;
    }

    public OReportDetail(Serializable self, Serializable[] targets, String effect, Object effectData) {
        this.self = self;
        this.targets = targets;
        this.effect = effect;
        this.effectData = effectData;
    }

    public Serializable getSelf() {
        return self;
    }

    public void setSelf(Serializable self) {
        this.self = self;
    }

    public Serializable[] getTargets() {
        return targets;
    }

    public void setTargets(Serializable[] targets) {
        this.targets = targets;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Object getEffectData() {
        return effectData;
    }

    public void setEffectData(Object effectData) {
        this.effectData = effectData;
    }
}
