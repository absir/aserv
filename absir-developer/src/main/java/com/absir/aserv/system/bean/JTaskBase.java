package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiActive;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Created by absir on 16/8/15.
 */
@MaEntity(parent = {@MaMenu("任务配置")}, name = "任务")
@Entity
public abstract class JTaskBase extends JbBeanS implements JiActive {

    @JaLang(value = "任务名称", tag = "taskName")
    private String name;

    @JaLang("任务数据")
    @Lob
    @Column(length = 10240)
    private byte[] taskData;

    @JaLang("开始时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long beginTime;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getTaskData() {
        return taskData;
    }

    public void setTaskData(byte[] taskData) {
        this.taskData = taskData;
    }

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
