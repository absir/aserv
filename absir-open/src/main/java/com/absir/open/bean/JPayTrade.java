/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-12 下午2:05:05
 */
package com.absir.open.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.open.bean.value.JePayStatus;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("支付管理")}, name = "订单")
@Entity
public class JPayTrade extends JbBase {

    @JaLang(value = "订单号", tag = "tradeId")
    @Id
    private String id;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST, listColType = 1)
    //@Temporal(TemporalType.TIMESTAMP)
    private long createTime;

    @JaLang(value = "平台名称", tag = "platformName")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String platform;

    @JaLang("平台参数")
    private String platformData;

    @JaLang("渠道")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String channel;

    @JaLang("交易号")
    @JaEdit(groups = JaEdit.GROUP_SEARCH)
    private String tradeNo;

    @JaLang("交易票据")
    @Column(length = 1024)
    private String tradeReceipt;

    @JaLang(value = "交易状态", tag = "tradeStatus")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private JePayStatus status;

    @JaLang(value = "交易状态参数", tag = "tradeStatusData")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String statusData;

    @JaLang(value = "商品编号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String goodsId;

    @JaLang(value = "商品数量")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int goodsNumber;

    @JaLang(value = "商品参数")
    private String goodsData;

    @JaLang(value = "订单参数")
    private int tradeData;

    @JaLang("金额")
    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    private float amount;

    @JaLang("用户编号")
    @JaEdit(groups = JaEdit.GROUP_LIST, listColType = 1)
    private long userId;

    @JaLang("服务编号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long serverId;

    @JaLang("角色编号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long playerId;

    @JaLang(value = "资源")
    private String source;

    @JaLang("配置编号")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int configureId;

    @JaLang("沙盒")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean sandbox;

    @JaLang(value = "更多数据")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private String[] moreDatas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformData() {
        return platformData;
    }

    public void setPlatformData(String platformData) {
        this.platformData = platformData;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeReceipt() {
        return tradeReceipt;
    }

    public void setTradeReceipt(String tradeReceipt) {
        this.tradeReceipt = tradeReceipt;
    }

    public JePayStatus getStatus() {
        return status;
    }

    public void setStatus(JePayStatus status) {
        this.status = status;
    }

    public String getStatusData() {
        return statusData;
    }

    public void setStatusData(String statusData) {
        this.statusData = statusData;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(String goodsData) {
        this.goodsData = goodsData;
    }

    public int getTradeData() {
        return tradeData;
    }

    public void setTradeData(int tradeData) {
        this.tradeData = tradeData;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getConfigureId() {
        return configureId;
    }

    public void setConfigureId(int configureId) {
        this.configureId = configureId;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public String[] getMoreDatas() {
        return moreDatas;
    }

    public void setMoreDatas(String[] moreDatas) {
        this.moreDatas = moreDatas;
    }
}
