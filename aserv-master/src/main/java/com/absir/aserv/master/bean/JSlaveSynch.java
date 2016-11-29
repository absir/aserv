/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月6日 上午9:28:38
 */
package com.absir.aserv.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanSS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.orm.value.JaColum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "同步", value = @MaMenu(order = -127))
@JaModel(desc = true)
@Entity
public class JSlaveSynch extends JbBeanSS {

    @JaLang("链接")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String uri;

    @JaLang("提交数据")
    @Lob
    @Column(length = 10240)
    private byte[] postData;

    @JaLang("压缩")
    private boolean varints;

    @JaLang("更新时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime", editable = JeEditable.LOCKED)
    private long updateTime;

    @JaLang("已经同步")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    @JaColum(indexs = @Index(columnList = "synched"))
    private boolean synched;

    @JaLang(value = "自动同步", tag = "autoSynch")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    private boolean slaveAutoSynch;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public byte[] getPostData() {
        return postData;
    }

    public void setPostData(byte[] postData) {
        this.postData = postData;
    }

    public boolean isVarints() {
        return varints;
    }

    public void setVarints(boolean varints) {
        this.varints = varints;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isSynched() {
        return synched;
    }

    public void setSynched(boolean synched) {
        this.synched = synched;
    }

    public boolean isSlaveAutoSynch() {
        return slaveAutoSynch;
    }

    public void setSlaveAutoSynch(boolean slaveAutoSynch) {
        this.slaveAutoSynch = slaveAutoSynch;
    }

}
