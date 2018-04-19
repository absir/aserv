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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("支付管理")}, name = "支付")
@Entity
public class JPayHistory extends JbBase {

    @JaLang(value = "订单号", tag = "tradeId")
    @Id
    private String id;

    @JaLang("交易号")
    @Column(unique = true)
    private String tradeNo;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST, listColType = 1)
    private int createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
