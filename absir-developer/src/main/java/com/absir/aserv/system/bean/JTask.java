package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbVerifier;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiActive;

import javax.persistence.Entity;

/**
 * Created by absir on 16/8/15.
 */
@MaEntity(parent = {@MaMenu("任务配置")}, name = "任务")
@Entity
public class JTask extends JbVerifier implements JiActive {

    @JaLang("开始时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long beginTime;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }
}
