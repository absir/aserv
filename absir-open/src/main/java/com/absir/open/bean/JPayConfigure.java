package com.absir.open.bean;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.core.kernel.KernelString;
import org.hibernate.annotations.Type;

import java.util.Map;

@MaEntity(parent = {@MaMenu("支付管理")}, name = "支付")
public class JPayConfigure extends JConfigureBase {

    @JaLang("渠道金额映射")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, Float> amountDict;

    public Map<String, Float> getAmountDict() {
        return amountDict;
    }

    public void setAmountDict(Map<String, Float> amountDict) {
        this.amountDict = amountDict;
    }

    public boolean validatePayAmount(JPayTrade payTrade, float payAmount, String productId) {
        if (payAmount >= payTrade.getAmount()) {
            payTrade.setAmount(payAmount);
            return true;
        }

        if (!KernelString.isEmpty(productId) && amountDict != null) {
            Float orgAmount = amountDict.get(payTrade.getPlatform() + "@" + productId);
            if (orgAmount != null) {
                if (orgAmount >= payTrade.getAmount()) {
                    payTrade.setRealAmount(payAmount);
                    return true;
                }
            }
        }

        return false;
    }
}
